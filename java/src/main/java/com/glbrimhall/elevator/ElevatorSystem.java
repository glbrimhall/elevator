/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author geoff
 */
public class ElevatorSystem {

    protected static boolean                debug = false;
    protected static int                    maxFloors = 0;
    protected List<RunningElevator>    elevatorList = null;
    protected int                           rotateElevator = 0;
    protected boolean                       running = true;
    protected String                        lastReport = null;

    public ElevatorSystem()
    {
        elevatorList = Collections.synchronizedList(new LinkedList< RunningElevator >() );
        lastReport = new String();
    }

    public static boolean isDebugging() { return debug; }

    public List< RunningElevator > getElevatorList() { return elevatorList; }

    public boolean isInitialized() { return ! elevatorList.isEmpty(); }
    
    public boolean isRunning() {

        if ( running && ! isInitialized() ) {
            return true;
        }
        
        boolean online = false;
        
        for( RunningElevator running: elevatorList ) {
            if ( ! running.elevator.isOffline() ) {
                online = true;
            }
        }
        return online;
    }

    public void Initialize( int numElevators, int numFloors ) {
        maxFloors = numFloors;

        // Create the elevator objects
        if ( numElevators <= 0 ) { numElevators = 1; }
        
        int elevatorNumber = 1;
        
        while ( numElevators > 0 )
        {
            elevatorList.add( new RunningElevator( elevatorNumber ) );
            --numElevators;
            ++elevatorNumber;
        }
        
        for( RunningElevator elevator: elevatorList ) {
            elevator.thread.start();
        }
    }
   
    public void Shutdown() {

        for( int i = 0; i < elevatorList.size(); ++i ) {
            maintenenceRequest( i );
        }
        
        running = false;
    }

    public static int getMaxFloors() { return maxFloors; }

    public Elevator randomElevator() {
        Elevator e = elevatorList.get( rotateElevator ).elevator;
        rotateElevator++;
        rotateElevator %= elevatorList.size();
        return e;
    }
    
    public Elevator getElevator( int num ) {
        if ( num < 0 ) {
             num = 0;
        }
        if ( num < elevatorList.size() ) {
            return elevatorList.get( num ).elevator; 
        }
        return randomElevator();
    }
    
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
     * This method assigns an external floor request to an elevator.
     * @param floor the floor the request came from
     * @param direction the direction of the request
     * @return which elevator took the request. -1 may be returned if no elevator
     *         took the request.
     */
    
   public int floorRequest( int floor, Movement direction )
   {
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
           Elevator elevator = elevatorList.get(i).elevator;
           
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
    * reports Status
    */
   public void reportStatus() {
       
       StringBuffer report = new StringBuffer();

       if ( ! isInitialized() ) {
           report.append( "ElevatorSystem not initialized.\n" );
       }
       else {
           for( int i = 0; i < elevatorList.size(); ++i ) {
               report.append( elevatorList.get( i ).elevator.reportStatus() );
           }
           //report.append( ElevatorQueue.reportQueues( elevatorList, 3) );
       }
       
       String currentReport = report.toString();
       
       if ( ! currentReport.equals( lastReport ) ) {
           System.out.print( currentReport.toString() );
           lastReport = currentReport;
       }
   }
   
    /**
     * This method needs to be in it's own thread, with a queue of requests
     * because elevators may not automatically say they are available to take on
     * a request, so just put the request in a queue, keep looping this algorithm.
     * @param whichElevator the elevator to put into Maintenence
     * @return which elevator took the request. -1 may be returned if no elevator
     *         took the request.
     */
   public int maintenenceRequest( int whichElevator )
   {
       Elevator offline_elevator = elevatorList.get( whichElevator ).elevator;
       
       // This puts floor zero on the queue for the elevator, setInMaintenece
       // will keep it from accepting any further requests so it will 
       // gracefully empty out it's queue and then go to the bottom floor.
       offline_elevator.buttonPressed( 0 );
       offline_elevator.setInMaintenence( true );
        
       return 0;
   }

   public static void main( String[] args ) {
       
       ElevatorSystem   system = new ElevatorSystem();
       ParseList        parser = new ParseList( system );
       Thread           parseThread = new Thread( parser );

       ParseCommand.setElevatorSystem( system );
       
       // Parse over command line args:
       for ( String arg: args ) {
           parser.Parse( arg );
       }

       parseThread.start();
       
       while( system.isRunning() ) {
           system.reportStatus();
           ElevatorSystem.sleepSeconds( system.getClass().getName(), 1 );
       }
       system.reportStatus();
       System.out.print( "ElevatorSystem Offline.\n" );
       System.exit(0);
   }
}
