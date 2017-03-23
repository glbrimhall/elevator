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

import static com.glbrimhall.elevator.ParseCommand.OK;
import static com.glbrimhall.elevator.ParseCommand.getElevatorSystem;
import java.util.regex.Matcher;

/**
 * The ParseFloor class maps the "f[floorNum][up|down]" 
 * user input to ElevatorSystem.floorRequest( floorNum, [up|down] )
 */
public class ParseFloor extends ParseCommand {

    public ParseFloor() {
        super( "f(\\d+)(u|d)+", "floor <num> up or down: example f3u means floor 3 up" );
    }

    @Override
    public String Parse( String cmd ) {
        Matcher m = compiledPattern.matcher( cmd );

        while( m.find() ) {
            int         floor = Integer.parseInt( m.group( 1 ) );
            String      upordown = m.group( 2 );
            Movement    direction = Movement.UP;
            
            // Note we default to going up !
            if ( upordown.charAt( 0 ) == 'd' ) {
                direction = Movement.DOWN;
            }
            getElevatorSystem().floorRequest( floor, direction );
        }
        return OK;
    }
}
