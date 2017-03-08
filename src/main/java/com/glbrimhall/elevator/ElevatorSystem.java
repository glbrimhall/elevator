/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author geoff
 */
public class ElevatorSystem {

    protected static int                    maxFloors = 10;
    protected static ArrayList< RunningElevator >  elevatorList = null;

    protected Thread                        requestThread = null;
    protected ExternalRequestQueue             floors = null;
    
    public ElevatorSystem( int numFloors, int numElevators )
    {
        maxFloors = numFloors;

        // Create the elevator objects
        if ( numElevators <= 0 ) { numElevators = 1; }
        
        elevatorList = new ArrayList< RunningElevator >();
        
        while ( numElevators > 0 )
        {
            elevatorList.add( new RunningElevator() );
            --numElevators;
        }
        
        // Create the floor request Queue:
        floors = new ExternalRequestQueue();
        requestThread = new Thread( floors );
    }
   
    public static int getMaxFloors() { return maxFloors; }
    
    public static ArrayList<RunningElevator> getElevatorList() {
        return elevatorList;
    }

    public static int sleepSeconds( String className, int seconds ) 
    {
       if ( seconds < 1 ) { seconds = 1; }
       try {
            Thread.sleep( 1000 );
        } catch (InterruptedException ex) {
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
    }

    
 
    
   /**
     * This method needs to be in it's own thread, with a queue of requests
     * because elevators may not automatically say they are available to take on
     * a request, so just put the request in a queue, keep looping this algorithm.
     * @param floor the floor the request came from
     * @param direction the direction of the request
     * @return which elevator took the request. -1 may be returned if no elevator
     *         took the request.
     */
    
   public int floorRequest( int floor, Movement direction )
   {
        floors.requestElevator( new FloorRequest( floor, direction ) );
   }

    /**
     * This method needs to be in it's own thread, with a queue of requests
     * because elevators may not automatically say they are available to take on
     * a request, so just put the request in a queue, keep looping this algorithm.
     * @param floor the floor the request came from
     * @param direction the direction of the request
     * @return which elevator took the request. -1 may be returned if no elevator
     *         took the request.
     */
   public int maintenenceRequest( int whichElevator )
   {
       Elevator offline_elevator = elevatorList.get( whichElevator ).elevator;
       
       // This puts floor zero on the queue for the elevator, setInMaintenece
       // will keep it from accepting any further requests so it will 
       // gracefully empty out it's queue and then go to the bottom floor.
       offline_elevator.floorButtonPressed( 0 );
       offline_elevator.setInMaintenence( true );
   }

}
