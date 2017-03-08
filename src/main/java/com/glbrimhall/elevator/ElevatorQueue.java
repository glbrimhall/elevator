/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

/**
 *
 * @author geoff
 */
public class ElevatorQueue {
    protected SortedSet< FloorRequest > requestedFloors = null;
    protected Movement                  currentMovement;
    protected FloorRequest              currentFloor = null;
    protected Iterator                  moveFloor = null;
    

    public ElevatorQueue() 
    {
        this.requestedFloors = Collections.synchronizedSortedSet( new TreeSet< FloorRequest >() );
        this.moveFloor = this.requestedFloors.iterator();
        this.currentMovement = Movement.UP;
    }
    
    public FloorRequest getCurrentFloor() { return currentFloor; }
    
    /** 
     * Returns the next floor the elevator will stop at.
     * @return the next floor, or -1 if it has nothing left in current direction
     */
    public int getNextFloor()
    {
        if ( requestedFloors.isEmpty() )
            { return -1; }
        
        // Java sucks ! can't deep copy moveFloor:
        Iterator< FloorRequest > nextFloor = requestedFloors.iterator();
        
        while( nextFloor.hasNext() )
        {
            if ( nextFloor.next().equals( currentFloor ) )
                { break; }
        }

        if ( nextFloor.hasNext() )
            {
                return nextFloor.next().floor;
            }
        return -1;
    }

    public void addFloor( FloorRequest newFloor )
    {
        requestedFloors.add( newFloor );
    }
        
    public boolean containsFloor( FloorRequest newFloor )
    {
        return requestedFloors.contains( newFloor );
    }

    public int moveFloor()
    {
        if ( moveFloor.hasNext() )
        {
            currentFloor = moveFloor.next();
            moveFloor.remove();
        }
        else
        {
            // We are at the top of our floor, reset back to the bottom
            moveFloor = requestedFloor.iterator();
        }
    }
}
