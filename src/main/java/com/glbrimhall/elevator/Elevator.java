/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author geoff
 */
public class Elevator implements Runnable {

    // Data Members
    protected ElevatorQueue             queue = null;
    protected long                      traversedFloors;
    protected long                      numRequests;
    protected int                       defaultDoorOpenWaitSeconds;
    protected boolean                   inMaintenence;
    protected boolean                   moving;
    
    // Methods Members
    public Elevator()
    {
        queue = new ElevatorQueue();
        this.traversedFloors = 0;
        this.numRequests = 0;
        this.defaultDoorOpenWaitSeconds = 30;
        this.inMaintenence = false;
        this.moving = false;
    }
    
    @Override
    public void run()
    {
        // At system startup assume elevator is waiting with door open at 
        // floor zero, waiting for first floor request:
        stopAndOpenDoors(0);
        
        while( ! isInMaintenence() )
        {
            movingToNextFloor();
        }
    }
        
    public boolean isInMaintenence()  { return inMaintenence; }
    public synchronized boolean setInMaintenence(boolean new_value) 
                                 { return inMaintenence = new_value; }
    public int getCurrentFloor() { return queue.getCurrentFloor().floor; }
    public boolean isMoving()    { return moving; }
    public long getTraversedFloors() { return traversedFloors; }
    public long getNumRequests() { return numRequests; }
    public SortedSet getRequestedFloors() { return requestedFloors; }
    
    /** 
     * Returns the next floor the elevator will stop at.
     * @return the next floor, or -1 if it has nothing left in current direction
     */
    public int getNextFloor()
    {
        if ( isInMaintenence() )
            { return -1; }

        return queue.getNextFloor();
    }

    protected synchronized boolean setMoving(boolean new_value) 
                                 { return moving = new_value; }
    protected Movement getCurrentMovement() { return currentMovement; }
    protected synchronized Movement setCurrentMovement(Movement new_movement) 
                                 { return currentMovement = new_movement; }
    

    public void reportStatus( int elevatorNumber )
    {
        System.out.format( "ELEVATOR[%d] at floor %d ", elevatorNumber, getCurrentFloor() );
        
        if ( isMoving() )
        {
            System.out.print( "moving. " );
        }
        else
        {
            System.out.print( "waiting. " );
        }

        System.out.format( "Next floor: %d, requested floors: %s\n", getNextFloor(),
                requestedFloors.toArray().toString() );
            
    }

    /** 
     * This method assumes the control panel for the floor buttons inside the
     * elevator serializes the requests even if a number of people inside
     * the elevator appear to push floor buttons at the same time.
     * 
     * @param floor    the requested floor
     * @return         boolean indicating whether the floor was added
     *                 to the requestedFloors queue. If false is returned the
     *                 pressed floor button should not be lit up.
     */
    public boolean floorButtonPressed(int floor)
    {
        if ( isInMaintenence() )
            { return false; }

        int currentFloor = getCurrentFloor();
        
        if ( currentFloor == floor ) { return false; }

        // Requirements check: should numRequests reflect number of times
        // people pushed buttons or number of times button press resulted
        // in actually being added to queue. Currented coded for the latter.
        ++numRequests;

        if ( currentFloor < floor )
        {
            queue.addFloor( new FloorRequest( floor, Movement.DOWN ) );
        }
        else
        {
            queue.addFloor( new FloorRequest( floor, Movement.UP ) );
        }
            
        return true;
    }
    
    /**
     * Is the elevator stopping at a floor moving in a given direction ?
     * @param request the floor and direction request
     * @return the distance from the elevator to the floor. NOTE it
     *         may return -1 if the elevator isn't stopping at the floor
     */
    public int isStoppingAtFloor( FloorRequest request )
    {
        if ( isInMaintenence() )
            { return -1; }
        
        if ( ! queue.containsFloor( request ) )
            { return -1; }
        
        return Math.abs( getCurrentFloor() - request.floor );
    }
    
        /**
     * Calculates the distance of the elevator from an external request
     * on a floor. 
     * @param request the floor and direction request
     * @return the distance from the elevator to the request. NOTE it
     *         may return -1 if the elevator currently can't handle the request
     */
    public int requestDistance( FloorRequest request )
    {
        if ( isInMaintenence() )
            { return -1; }

        // Some yahoo is just pressing the "open" button with the elevator
        // already at the floor they are on, or elevator is moving by the floor
        // but beyond the point of being able to stop
        if ( getCurrentFloor() == floor )
            { return -1; }
        
        // Elevator is stopped at a floor with doors open with no more floor
        // requests queued, just waiting for someone walk in or get a request
        // from another floor.
        if ( requestedFloors.isEmpty() )
            { return Math.abs( getCurrentFloor() - floor ); }

        if ( direction == getCurrentDirection() )
        { 
            if ( direction == Movement.UP )
            {
                // if elevator is below requested floor and the elevator is coming up
                if ( getCurrentFloor() < floor )
                    { return floor - getCurrentFloor(); }
                else
                // elevator has passed requesting floor going up, 
                // give distance from endpoint
                    { return ((int)requestedFloors.last()) - floor; }
            }
            else
            if ( direction == Movement.DOWN )
            {
                // if elevator is above requested floor and the elevator is coming down
                if ( getCurrentFloor() > request.floor )
                    { return getCurrentFloor() - request.floor; }
                else
                // elevator has passed requesting floor going down, 
                // give distance from endpoint
                    { return floor - ((int)requestedFloors.first()); }
            }
        }
        // If eleveator is not already heading in the requsted direction,
        // return the distence of the endpoint floor for that direction
        else
        { 
            if ( direction == Movement.UP )
            {
                int first_floor = (int)requestedFloors.first();
                // if elevator is above requesting floor and the elevator is coming down
                if ( first_floor >= floor )
                    { return first_floor - floor; }
                else
                // We don't want elevator to stop for a person to get on wanting
                // to go up, but it still goes down on them !
                    { return -1; }
            }
            else
            if ( request.direction == Movement.DOWN )
            {
                int last_floor = (int)requestedFloors.last();
                // if elevator is below requesting floor and the elevator is coming up
                if ( last_floor <= floor )
                    { return floor - last_floor; }
                else
                // We don't want elevator to stop for a person to get on wanting
                // to go down, but it still goes up on them !
                    { return -1; }
            }
        }
        
        return -1;
    }
    
    /** 
     * This method should return indicating the doors to the elevator have 
     * safely closed. 
     * @param waitSeconds   number of seconds to wait with doors open.
     * @return              the actual time the elevator waited before closing 
     *                      doors. It may be longer than waitSeconds if people
     *                      held the doors, or no much longer if empty and there 
     *                      are no current requests for another floor.
     */
    protected int waitUntilDoorsClose( int waitSeconds ) 
    {
        if ( waitSeconds < 0 ) 
            { waitSeconds = defaultDoorOpenWaitSeconds; }
        
        int actualSeconds = 0;
        
        setIsMoving( false );

        // This logic IS INCOMPLETE, needs more fleshing out to wait properly
        // incase people hold door open.
        // Note if a person comes into an elevator but never presses a floor 
        // button eleveator will just sit there ! 
        while( requestedFloors.isEmpty() || actualSeconds < waitSeconds )
        {
            ElevatorSystem.sleepSeconds( getClass().getName(), 1 );
            
            ++actualSeconds;
        }
        
        setIsMoving( true );

        return actualSeconds;
    }
    
    /** 
     * This method should be called if an elevator should stop and open it's 
     * doors at a floor.
     */
    protected boolean stopAndOpenDoors( int waitSeconds ) throws Exception 
    { 
        if ( 0 == waitSeconds )
        {
            waitSeconds = defaultDoorOpenWaitSeconds;
        }

        // Record current direction, will use later to calculate new direction
        Movement currentMovement = getCurrentMovement();        
        
        requestedFloors.remove( getCurrentFloor() );
        boolean requestedFloorsEmpty = requestedFloors.isEmpty();
                
        // This function guarentees to not return unless there is a request
        // to move to a different floor, requestedFloors.size() > 1
        waitUntilDoorsClose( waitSeconds );
        
        SortedSet aboveFloors = requestedFloors.tailSet( getCurrentFloor() );
        SortedSet belowFloors = requestedFloors.headSet( getCurrentFloor() );

        // If we had emptied out the queue, set direction based on closest
        // requested floor. Code gives higher weight to going up.
        if ( requestedFloorsEmpty )
        {
            // TODO: add secondaryFloors elements to the requestFloors.
            // TODO: automatically do this if at end: top or bottom floor
            

            int above_distance = ElevatorSystem.getMaxFloors();
            int below_distance = ElevatorSystem.getMaxFloors();
            
            if ( ! aboveFloors.isEmpty() )
            {
                above_distance = ((int)aboveFloors.first()) - getCurrentFloor();
                setCurrentMovement( UP );
            }
            
            if ( ! belowFloors.isEmpty() )
            {
                below_distance = getCurrentFloor() - ((int)belowFloors.last());
                
                if ( below_distance < above_distance )
                    { setCurrentMovement( DOWN );}
            }
        }
        else
        // At this point the requestedFloors was not empty, we need prefer
        // the direction we had been going but double check that we still have
        // floors requested in that direction.
        {
            if ( currentMovement == Movement.UP )
            {
                if ( ! aboveFloors.isEmpty() )
                   { setCurrentMovement( Movement.UP ); }
                else
                   { setCurrentMovement( Movement.DOWN ); }
            }
            else
            if ( currentMovement == Movement.DOWN )
            {
                if ( ! belowFloors.isEmpty() )
                   { setCurrentMovement( Movement.DOWN ); }
                else
                   { setCurrentMovement( Movement.UP ); }
            }
            else
            {
                // This should never happen, throw an exception
                throw( new Exception( "stopAndOpenDoors Unexpected state" ) );
            }
            
        }

        return true; 
    }
    
    /** 
     * This method should be called as the elevator is coming up to each floor
     * as it moves.
     * 
     * @return  boolean indicating normal movement of elevator happened
     *          as it approached a floor. False should really throw an exception
     *          because something catastrophic happened
     */
    protected boolean movingToNextFloor()
    {
        ++traveresedFloors;
        
        queue.moveFloor();
        
        stopAndOpenDoors();
        
        return true;
    }
}
