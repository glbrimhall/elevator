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

import java.util.Objects;

/**
 *
 * @author glBrimhall
 * 
 * FloorRequest implements Comparable so that it could be used in a sorted container
 * It contains the floor number and direction ( UP or DOWN ) request.
 * The UP/DOWN are mapped to (+1/-1) * ( floor number ), 
 * so DOWN requests come before UP requests when sorted, ie DOWN 17 => -17.
 * The bottom most floor, 0, is always aligned to be UP.
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
    public String toString() {
        return "[ " + Integer.toString(floor) + " " + direction.name() + " ]";
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
        int this_val = this.floor;
        int that_val = that.floor;
        
        if ( this.direction == Movement.DOWN )
            { this_val = -this_val; }
        
        if ( that.direction == Movement.DOWN )
            { that_val = -that_val; }
        
        return Integer.compare( this_val, that_val );
    }
    
    public void copy(FloorRequest obj) {
        if (this == obj) {
            return;
        }
        
        floor = obj.floor;
        direction = obj.direction;
    }
}
