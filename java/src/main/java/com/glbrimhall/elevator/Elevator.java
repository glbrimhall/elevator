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

import java.util.Formatter;

/**
 *
 * @author glBrimhall
 * 
 * Elevator represents a single elevator, containing it's ElevatorQueue. It 
 * manages movement, the opening/closing of doors, buttons pressed within an
 * elevator and whether an elevator is in maintenence.
 * 
 */
public class Elevator implements Runnable {

    // Data Members
    protected ElevatorQueue             queue = null;
    protected long                      traversedFloors;
    protected long                      numRequests;
    protected int                       defaultDoorOpenWaitSeconds;
    protected boolean                   inMaintenence;
    protected boolean                   moving;
    protected int                       elevatorNumber;
    
    // Methods Members
    public Elevator( int number )
    {
        queue = new ElevatorQueue();
        this.traversedFloors = 0;
        this.numRequests = 0;
        this.defaultDoorOpenWaitSeconds = 3;
        this.inMaintenence = false;
        this.moving = false;
        this.elevatorNumber = number;
    }
    
    @Override
    public void run()
    {
        // At system startup assume elevator is waiting with door open at 
        // floor zero, waiting for first floor request:
        stopAndOpenDoors(0);
        
        while( ! isOffline() )
        {
            movingToNextFloor();
        }
    }
        
    public ElevatorQueue getQueue() { return queue; }
    public boolean isInMaintenence()  { return inMaintenence; }
    public boolean isOffline()  { return inMaintenence && queue.isEmpty()
                                         && 0 == getCurrentFloor(); }
    public synchronized boolean setInMaintenence(boolean new_value) 
                                 { return inMaintenence = new_value; }
    public int getCurrentFloor() { return queue.getServicing().floor; }
    public Movement getCurrentDirection() { return queue.getServicing().direction; }
    public boolean isMoving()    { return moving; }
    public long getTraversedFloors() { return traversedFloors; }
    public long getNumRequests() { return numRequests; }
    
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

    public String reportStatus()
    {
        StringBuilder report = new StringBuilder();
        Formatter     format = new Formatter(report);
        
        format.format("ELEVATOR[%2d] at floor %3d ", elevatorNumber, getCurrentFloor() );
        format.flush();
        
        if ( isOffline() )
        {
            report.append( "offline. " );
        }
        else
        if ( isMoving() )
        {
            report.append( "moving. " );
        }
        else
        {
            report.append( "waiting. " );
        }

        report.append( "QUEUE " );
        report.append( getQueue().report() );
        report.append( "\n" );
        return report.toString();
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
    public boolean buttonPressed(int floor)
    {
        if ( isInMaintenence() )
            { return false; }

        int currentFloor = getCurrentFloor();
        
        if ( currentFloor == floor ) { return false; }

        // Requirements check: should numRequests reflect number of times
        // people pushed buttons or number of times button press resulted
        // in actually being added to queue. Currented coded for the latter.
        ++numRequests;

        if ( currentFloor > floor )
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
     * Calculates the distance of the elevator from an external request
     * on a floor. 
     * @param request the floor and direction request
     * @return the distance from the elevator to the request. NOTE it
     *         may return -1 if the elevator currently can't handle the request
     */
    public int requestDistance( FloorRequest request )
    {
        return queue.distanceToFloor( request );
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
        if ( waitSeconds <= 0 ) 
            { waitSeconds = defaultDoorOpenWaitSeconds; }
        
        int actualSeconds = 0;
        
        setIsMoving( false );

        // This logic IS INCOMPLETE, needs more fleshing out to wait properly
        // incase people hold door open.
        // Note if a person comes into an elevator but never presses a floor 
        // button eleveator will just sit there ! 
        while( ( queue.isEmpty() || actualSeconds < waitSeconds ) && ! isOffline() )
        {
            ElevatorSystem.sleepSeconds( getClass().getName(), 1 );
            
            ++actualSeconds;
        }
        
        setIsMoving( true );

        return actualSeconds;
    }
    
    protected synchronized boolean setIsMoving(boolean new_value) 
                                 { return moving = new_value; }

    protected boolean stopAndOpenDoors( int waitSeconds )
    { 
        // This function guarentees to not return unless there is a request
        // to move to a different floor, requestedFloors.size() > 1
        waitUntilDoorsClose( waitSeconds );

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
        ++traversedFloors;
        
        queue.moveFloor();
        
        stopAndOpenDoors( 0 );
        
        return true;
    }
}
