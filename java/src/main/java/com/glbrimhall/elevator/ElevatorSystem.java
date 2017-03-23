/*
 * Copyright (C) 2017 glbrimhall.com
 *
 * This file is part of an Elevator Simulator program written in java.
 *
 * Elevator Simulator program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.glbrimhall.elevator;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ElevatorSystem is the controlling class containing an array of Elevators  
 * handling initialization, shutdown, maintenence, floor requests, etc.
 */
public class ElevatorSystem {

    protected static boolean                debug = false;
    protected static int                    maxFloors = 0;
    protected List<Elevator>                elevatorList = null;
    protected int                           rotateElevator = 0;
    protected boolean                       running = true;
    protected String                        lastReport = null;

    public ElevatorSystem()
    {
        elevatorList = Collections.synchronizedList(new LinkedList< Elevator >() );
        lastReport = new String();
    }

    /** 
     * Indicates if the ElevatorSystem is elevator in debug mode, with more
     * verbose debugging output.
     */
    public static boolean isDebugging() { return debug; }

    /** 
     * Returns the list of elevators being managed.
     */
    public List< Elevator > getElevatorList() { return elevatorList; }
    
    /** 
     * Indicates the {@link #initialize(int, int)} method has been called.
     */
    public boolean isInitialized() { return ! elevatorList.isEmpty(); }
    
    /** 
     * Indicates the ElevatorSystem has been initialized and at least one
     * elevator is not offline.
     */
    public boolean isRunning() {

        if ( running && ! isInitialized() ) {
            return true;
        }
        
        boolean online = false;
        
        for( Elevator elevator: elevatorList ) {
            if ( ! elevator.isOffline() ) {
                online = true;
            }
        }
        return online;
    }

    /** 
     * Indicates if the elevator is initialized minimally with a number of
     * elevators and the top floor number allowed by all the elevators.
     * 
     * @param numElevators indicate how many elevators to manage
     * @param numFloors the total floors serviced by all the elevators.
     */
    public void initialize( int numElevators, int numFloors ) {
        maxFloors = numFloors;

        // Create the elevator objects
        if ( numElevators <= 0 ) { numElevators = 1; }
        
        int elevatorNumber = 1;
        
        while ( numElevators > 0 )
        {
            elevatorList.add( new Elevator( elevatorNumber ) );
            --numElevators;
            ++elevatorNumber;
        }
        
        for( Elevator elevator: elevatorList ) {
            elevator.getThread().start();
        }
    }

    /** 
     * Puts all the elevators in the ElevatorSystem into maintenence mode.
     */
    public void shutdown() {

        running = false;

        for( int i = 0; i < elevatorList.size(); ++i ) {
            maintenenceRequest( i );
        }
        
    }

    /** 
     * Returns the total number of floors serviced by the ElevatorSystem.
     */
    public static int getMaxFloors() { return maxFloors; }

    /** 
     * Picks a "random" elevator when there is not a better weighted choice.
     * The current "random" selector is just a round robin.
     */
    public Elevator randomElevator() {
        Elevator e = elevatorList.get( rotateElevator );
        rotateElevator++;
        rotateElevator %= elevatorList.size();
        return e;
    }
    
    /** 
     * Returns the zero based elevator at index. If the index is out of bounds
     * returns a {@link #randomElevator()}
     * 
     * @param num the elevator index number.
     */
    public Elevator getElevator( int num ) {
        if ( 0 <= num && num < elevatorList.size() ) {
            return elevatorList.get( num ); 
        }
        return randomElevator();
    }
    
    /** 
     * Helper function that puts the calling thread to sleep for a desired 
     * number of seconds.
     * 
     * @param className is the calling getClass().getName() if an exception
     * needs to get thrown.
     * @param seconds is the number of seconds to sleep
     */
    public static int sleepSeconds( String className, int seconds ) 
    {
       if ( seconds < 1 ) { seconds = 1; }
       try {
            Thread.sleep( 1000 * seconds );
        } catch (InterruptedException ex) {
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
       return 0;
    }

    /** 
     * Waits on all started threads to shutdown, closes down the input
     */
    public void waitToShutdown( ParseList parser ) 
    {
       try {
           for( Elevator elevator: elevatorList ) {
                elevator.getThread().join();

           parser.stop();
           }
        } catch (InterruptedException ex) {
            Logger.getLogger( getClass().getName() ).log( Level.SEVERE, null, ex );
        }
       return;
    }
    
   /**
     * This method assigns an external floor request to an elevator.
     * 
     * @param floor the floor the request came from
     * @param direction the direction of the request
     * @return which elevator took the request. -1 may be returned if no elevator
     *         took the request, which should only happen if the ElevatorSystem
     *         is not elevator.
     */
   public int floorRequest( int floor, Movement direction )
   {
       if ( ! running ) {
           return -1;
       }

       FloorRequest request = new FloorRequest( floor, direction );
       
       // Current implementation of requestDistance will always return a value
       // less than 2 * maxFloors.
       int       distance = ElevatorSystem.getMaxFloors() * 2;

       // This is cheating cause it's using knowledge of
       // internal implementation of randomElevator():
       int       which_elevator = rotateElevator;
       Elevator  selected_elevator = randomElevator();
       
       // Find the elevator which is closest in distance. Note this may
       // pick an elevator which did not have the floor assigned over one
       // that already did have the floor assigned because of how the distance
       // is calculated.
       for( int i = 0; i < elevatorList.size(); ++i )
       {
           Elevator elevator = elevatorList.get(i);
           
           if ( ! elevator.isInMaintenence() )
           {
               int elevator_distance = elevator.requestDistance( request );

               if ( elevator_distance < distance )
               {
                   distance = elevator_distance;
                   selected_elevator = elevator;
                   which_elevator = i;
               }
           }
       }

       selected_elevator.getQueue().addFloor( request );
       
       return which_elevator;
   }

   /**
    * Reports the status of the elevator system, currently to stdout
    */
   public void reportStatus() {
       
       StringBuffer report = new StringBuffer();

       if ( ! isInitialized() ) {
           report.append( "ElevatorSystem not initialized.\n" );
       }
       else {
           for( int i = 0; i < elevatorList.size(); ++i ) {
               report.append( elevatorList.get( i ).reportStatus() );
           }
           //report.append( ElevatorQueue.reportQueuqes( elevatorList, 3) );
       }
       
       String currentReport = report.toString();
       
       if ( ! currentReport.equals( lastReport ) ) {
           System.out.print( currentReport.toString() );
           lastReport = currentReport;
       }
   }
   
    /**
     * This puts an elevator at zero based index in maintenence.
     * @param whichElevator the elevator to put into maintenence
     * @return which elevator took the request. It could be different than 
     *         what was requested if whichElevator is out of bounds.
     */
   public int maintenenceRequest( int whichElevator )
   {
       Elevator offline_elevator = getElevator( whichElevator );
       
       /*
       if ( offline_elevator.getIndex() != whichElevator ) {
           return -1;
       }
       */
       
       // This puts floor zero on the queue for the elevator, setInMaintenece
       // will keep it from accepting any further requests so it will 
       // gracefully empty out it's queue and then go to the bottom floor.
       offline_elevator.buttonPressed( 0 );
       offline_elevator.setInMaintenence( true );
        
       return offline_elevator.getIndex();
   }

    /**
     * The main entry point for elevator the ElevatorSystem simulator
     * All parameters which could be typed in during runtime can be passed in 
     * as startup command line arguments.
     */
   public static void main( String[] args ) {
       
       ElevatorSystem   system = new ElevatorSystem();
       ParseList        parser = new ParseList( system );

       parser.start();
       
       // Parse over command line args:
       for ( String arg: args ) {
           parser.Parse( arg );
       }

       while( system.isRunning() ) {
           system.reportStatus();
           ElevatorSystem.sleepSeconds( system.getClass().getName(), 1 );
       }
       
       //system.waitToShutdown( parser );
       system.reportStatus();
       System.out.print( "ElevatorSystem Offline.\n" );
       System.out.flush();
       System.exit(0);
   }
}
