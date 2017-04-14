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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * ElevatorQueue contains the ordered queue of {@link FloorRequest}. It is designed
 * so that a forward iterator represents the current floor position and direction, 
 * once the end of the queue is reached it goes back to the beginning which 
 * will contain the floors to move down to. What makes this work is the ordering
 * implemented in {@link FloorRequest}
 */
public class ElevatorQueue {
    protected TreeSet< FloorRequest >   requestedFloors = null;
    protected FloorRequest              servicing = null;
    protected Iterator< FloorRequest >  moveFloor = null;
    protected static final FloorRequest zeroFloor = new FloorRequest( 0, Movement.UP );
    
    public ElevatorQueue() 
    {
        this.requestedFloors = new TreeSet< FloorRequest >();
        this.moveFloor = this.requestedFloors.iterator();
        this.servicing = new FloorRequest( 0, Movement.UP );
        this.servicing.copy( zeroFloor );
    }
    
    /** 
     * Returns the FloorRequest being serviced within the queue.
     */
    public FloorRequest getServicing() { return servicing; }

    /** 
     * Returns the ElevatorQueue is empty or not
     */
    public synchronized boolean isEmpty() { return requestedFloors.isEmpty(); }
    
    /** 
     * Returns the highest floor the elevator will stop at.
     * @return the next floor, or -1 if it has nothing left in current direction
     */
    public synchronized int getHighestFloor()
    {
        if ( requestedFloors.isEmpty() )
            { return servicing.floor; }
        
        return Math.max( requestedFloors.first().floor, requestedFloors.last().floor);
    }
    
    /** 
     * Returns the lowest floor the elevator will stop at.
     * @return the next floor, or -1 if it has nothing left in current direction
     */
    public synchronized int getLowestFloor()
    {
        if ( requestedFloors.isEmpty() )
            { return servicing.floor; }
        
        FloorRequest lowest_down = requestedFloors.floor( zeroFloor );
        FloorRequest lowest_up = requestedFloors.ceiling( zeroFloor );
        
        // Note mathematically both cannot be null if requestedFloors is not empty.
        if ( lowest_down == null ) { return lowest_up.floor; }
        if ( lowest_up == null ) { return lowest_down.floor; }

        return Math.min( lowest_down.floor, lowest_up.floor);
    }

    /** 
     * Returns the next floor the elevator will stop at.
     * @return the next floor, or -1 if it has nothing left in current direction
     */
    public synchronized int getNextFloor()
    {
        if ( requestedFloors.isEmpty() )
            { return -1; }
        
        FloorRequest next = requestedFloors.higher( servicing );
        
        if ( next != null ) 
            { return next.floor; }
        else
            { return requestedFloors.first().floor; }
    }

    /** 
     * Adds a FloorRequest to the ElevatorQueue
     */
    public synchronized void addFloor( FloorRequest newFloor )
    {
        boolean waiting = requestedFloors.isEmpty();

        requestedFloors.add( newFloor );
        
        if ( ElevatorSystem.isDebugging() ) {
            System.out.println( "Adding [" + newFloor.toString() + "]" );
        }

        if ( waiting ) {
            return;
        }

        moveFloor = requestedFloors.tailSet( servicing ).iterator();
    }

    /** 
     * Returns if the ElevatorQueue already contains a FloorRequest
     */
    public synchronized boolean containsFloor( FloorRequest newFloor )
    {
        return requestedFloors.contains( newFloor );
    }

    /** 
     * Bumps to the next floor request within the queue.
     */
    public synchronized void moveFloor()
    {
        if ( ! moveFloor.hasNext() )
        {
            // We are at the top of our floor, reset back to the bottom
            moveFloor = requestedFloors.iterator();
        }

        if ( moveFloor.hasNext() )
        {
            servicing.copy( moveFloor.next() );
            moveFloor.remove();
        }
    }

    /**
     * Calculates the distance of the elevator from an external request
     * on a floor. 
     * @param request the floor and direction request
     * @return the distance from the elevator to the request. NOTE it
     *         may return a negative value if elevator is already handling that request
     */
    public synchronized int distanceToFloor( FloorRequest request )
    {
        // Elevator is stopped at a floor with doors open with no more floor
        // requests queued, just waiting for someone walk in or get a request
        // from another floor.
        if ( requestedFloors.isEmpty() )
            { return Math.abs( servicing.floor - request.floor ); }
        
        int distance = -1;

        if ( request.direction == servicing.direction )
            { 
            
            if ( request.direction == Movement.UP &&
                 servicing.floor < request.floor )
                { distance = request.floor - servicing.floor; }
            else
            if ( request.direction == Movement.DOWN &&
                 servicing.floor > request.floor )
                { distance = servicing.floor - request.floor; }
            }
        
        if ( distance == -1 )
            {
            if ( servicing.direction == Movement.DOWN )
                {
                int lowestFloor = getLowestFloor();

                distance = Math.abs( request.floor - lowestFloor ) +
                       ( servicing.floor - lowestFloor );
                }
            else
                {
                int highestFloor = getHighestFloor();

                distance = Math.abs( highestFloor - request.floor ) +
                       ( highestFloor - servicing.floor );
                }
            }

        // Give extra weight if we are already stopping.
        if ( containsFloor( request ) )
            { --distance; }
        
        return distance;
    }
    
    /**
     * Returns a string representation of the queue.
     */
    public synchronized String report() {

        if ( requestedFloors.isEmpty() ) {
            return "[ ]";
        }

        StringBuilder    queue = new StringBuilder();
        FloorRequest    last = null;
        
        queue.append( "[" );

        Iterator< FloorRequest > floors = requestedFloors.iterator();
        
        while ( floors.hasNext() ) {
            FloorRequest current = floors.next();
            
            if ( last == null || last.direction != current.direction ) {
                queue.append(" ");
                queue.append( current.direction.toString() );
                queue.append(":");
                last = current;
            }
            else {
                queue.append( "," );
            }
                
            queue.append( " " );
            queue.append( current.floor );
        }
        queue.append( " ]" );
        return queue.toString();
    }

    /**
     * Returns a string representation of a list of ElevatorQueue. 
     * Note this is an expensive operation with a class thread block.
     */
    public synchronized static void reportQueues( List<Elevator> queues, int tab )
    {
        if ( tab < 1 )
            { tab = 4; }

        ArrayList< Iterator< FloorRequest > > elevatorList = new ArrayList();
        
        for( Elevator i: queues )
            { elevatorList.add( i.getQueue().requestedFloors.iterator() ); }
        
        int finished = elevatorList.size();
        
        while( finished > 0 )
            {
            for( int i = 0; i < elevatorList.size(); ++i )
                {
                Iterator< FloorRequest > elevator = elevatorList.get( i );
                if ( elevator.hasNext() )
                    {
                    System.out.format( elevator.next().toString() + tab );
                    }
                else
                    { --finished; }
                }
            System.out.format( "\n" );
            }
    }
}
