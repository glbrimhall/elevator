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
 * The ParseStatus class maps the "status" user input to 
 * {@link ElevatorSystem#reportStatus() }
 */
public class ParseStatus extends ParseCommand {

    public ParseStatus() {
        super( "status", "Report on the elevatorSystem status" );
    }

    @Override
    public String Parse( String cmd ) {

        elevatorSystem.reportStatus();

        return OK;
    }
}
