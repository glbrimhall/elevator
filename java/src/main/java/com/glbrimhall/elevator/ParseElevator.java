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

import java.util.regex.*;

/**
 * The ParseElevator class maps the "e[elevatorNum]f[floorButtonList]" 
 * user input to Elevator[elevatorNum].buttonPressed( floorButtonList )
 */
public class ParseElevator extends ParseCommand {

    public ParseElevator() {
        super( "e(\\d+)f([0-9,-]+)", "elevator <num> floorlist: example e2f2,4-6 means elevator 2 floor button 2, 4 through 6 pressed, note elevator is 1 based" );
    }

    @Override
    public String Parse( String cmd ) {
        Matcher m = compiledPattern.matcher( cmd );

        while( m.find() ) {
            int         elevatorNumber = Integer.parseInt( m.group( 1 ) ) - 1;
            Elevator    elevator = getElevatorSystem().getElevator( elevatorNumber );
            String[]    commaList = m.group( 2 ).split( "," );
            
            for( String floors: commaList ) {
                String[] floorList = floors.split( "-" );
                
                int startFloor = Integer.parseInt( floorList[ 0 ] );
                int endFloor = startFloor;
                
                if ( floorList.length > 1 ) {
                    endFloor = Integer.parseInt( floorList[ 1 ] );
                }
                
                if ( startFloor > endFloor ) {
                    int t = startFloor;
                    startFloor = endFloor;
                    endFloor = t;
                }
                
                for( ; startFloor <= endFloor; ++startFloor )
                    elevator.buttonPressed( startFloor );
                }
            }
        return OK;
    }
}

