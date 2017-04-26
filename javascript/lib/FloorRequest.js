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
"use strict";

var Movement = require( "Movement" );

/**
 * FloorRequest implements Comparable so that it could be used in a sorted container
 * It contains the floor number and direction ( UP or DOWN ) request.
 * The UP/DOWN are mapped to (+1/-1) * (floor number), 
 * so DOWN requests come before UP requests when sorted, ie DOWN 17 maps to -17.
 * The bottom most floor, 0, is always aligned to be UP.
 * This ordering has been chosen so that in the sorted container, after forward
 * iterating to the end, just reset to the beginning to service the DOWN 
 * requests from the top floor down to the bottom floor.
 */
function FloorRequest( floor, direction ) {
    this.floor = floor;
    if ( floor == 0 )
        { this.direction = Movement.UP; }
    else
        { this.direction = direction; }
}

FloorRequest.prototype.toString = function() {
    return "[ " + Integer.toString(floor) + " " + direction.name() + " ]";
}

FloorRequest.prototype.equals = new function(obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null) {
        return false;
    }

    if ( ! (obj instanceof FloorRequest) ) {
        return false;
    }
    if (this.floor != obj.floor) {
        return false;
    }
    if (this.direction != obj.direction) {
        return false;
    }
    return true;
}
    
FloorRequest.prototype.compareTo = function(obj) {
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
    
    FloorRequest.prototype.copy(FloorRequest obj) {
        if (this == obj) {
            return;
        }
        
        floor = obj.floor;
        direction = obj.direction;
    }
}
