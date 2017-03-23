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
 * The ParseNumberElevatorFloors class maps the "n[numElevator]f[numFloors]" 
 * user input to ElevatorSystem.initialize( numElevator, numFloors )
 */
public class ParseNumberElevatorFloors extends ParseCommand {

    public ParseNumberElevatorFloors() {
        super( "n(\\d+)f(\\d+)", "number elevator <num> floors <num>: example n3f12 means 3 elevators with 12 floors" );
    }

    @Override
    public String Parse( String cmd ) {
        Matcher m = compiledPattern.matcher( cmd );

        while( m.find() ) {
            int  numElevators = Integer.parseInt( m.group( 1 ) );
            int  numFloors = Integer.parseInt( m.group( 2 ) );

            getElevatorSystem().initialize( numElevators, numFloors );
        }
        return OK;
    }
}
