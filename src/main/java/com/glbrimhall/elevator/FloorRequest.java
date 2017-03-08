/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.Objects;

/**
 *
 * @author geoff
 * Implement Comparable so that it could be used in a sorted container
 */
public class FloorRequest implements Comparable< FloorRequest > {
    public int      floor;
    public Movement direction;

    public FloorRequest(int floor, Movement direction) {
        this.floor = floor;
        if ( floor == 0 )
            { this.direction = Movement.UP; }
        else
            { this.direction = direction; }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.floor;
        hash = 89 * hash + Objects.hashCode(this.direction);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FloorRequest other = (FloorRequest) obj;
        if (this.floor != other.floor) {
            return false;
        }
        if (this.direction != other.direction) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(FloorRequest obj) throws NullPointerException, ClassCastException {
        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            throw new NullPointerException("FloorRequest.CompareTo NullPointerException");
        }
        if (getClass() != obj.getClass()) {
            throw new ClassCastException("FloorRequest.CompareTo ClassCastException with " 
                                         + obj.getClass().getName() );
        }
        
        final FloorRequest that = (FloorRequest) obj;
        final int this_val = this.floor;
        final int that_val = that.floor;
        
        if ( this.direction == Movement.DOWN )
            { this_val = -this_val; }
        
        if ( that.direction == Movement.DOWN )
            { that_val = -that_val; }
        
        return Integer.compare( this_val, that_val );
    }
    
}
