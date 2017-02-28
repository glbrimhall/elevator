/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.ArrayList;

/**
 *
 * @author geoff
 */
public class FloorRequestQueue implements Runnable
{
    protected ArrayList< FloorRequest >     queue = null;
    protected boolean                       inMaintenence;

    public FloorRequestQueue() {
        this.inMaintenence = false;
        this.queue = new Collections.synchronizedList( ArrayList< FloorRequest > () );
    }

    public boolean isInMaintenence() {
        return inMaintenence;
    }

    public void setInMaintenence(boolean inMaintenence) {
        this.inMaintenence = inMaintenence;
    }
    
    /**
     * Submit a floor request. It will 
     * @param request the floor and direction of the request
     */
    
   public void requestElevator( FloorRequest request )
   {
       queue.add( request );
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
    
   protected int assignFloorRequest( FloorRequest request )
   {
       // logic to pick an elevator which is closest to the floor of the request
       // and coming towards the floor.
       
       int       distance = ElevatorSystem.getMaxFloors();
       Elevator  selected_elevator = null;
       int       which_elevator = -1;
       int       index = 0;
       
       // First see if an elevator is stopping on the requeste floor anyway
       // going the requested direction. If so, pick the one that is closest to
       // the floor.
       for( RunningElevator running: ElevatorSystem.getElevatorList() )
       {
           int elevator_distance = running.elevator.isStoppingAtFloor( request );

           if ( elevator_distance != -1 && elevator_distance < distance )
           {
               distance = elevator_distance;
               selected_elevator = running.elevator;
               which_elevator = index;
           }
           ++index;
       }
       
       if ( which_elevator != -1 )
            { return which_elevator; }

       // Find the elevator which is closest in distance. Note this may
       // not automatically happen because we want to avoid the situation where
       // a person gets on an elevator to go down, but it goes up a few floors 
       // first to finish it's directional queue (or vice-versa) !
       index = 0; 

       for( RunningElevator running: ElevatorSystem.getElevatorList() )
       {
           int elevator_distance = running.elevator.requestDistance( request );

           if ( elevator_distance != -1 && elevator_distance < distance )
           {
               distance = elevator_distance;
               selected_elevator = running.elevator;
               which_elevator = index;
           }
           ++index;
       }
       return which_elevator;
   }
   
    @Override
    public void run()
    {
        while( ! isInMaintenence() )
        {
            ListIterator request = queue.listIterator();
            
            while( request.hasNext() )
            {
                if ( -1 != assignFloorRequest( request ) )
                {
                    request.remove();
                }
            }
        }
    }
}
