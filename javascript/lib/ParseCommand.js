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
 * The ParseCommand implements IParseCommand, parent to the rest of the
 * ParseXXXX classes.
 */
public class ParseCommand implements IParseCommand {
    protected Pattern compiledPattern = null;
    protected String helpString = null;
    protected char token;
    protected static ElevatorSystem elevatorSystem;
    protected static final String OK = "OK";
    
    /**
     * Initializes a Parse object, setting the {@link IParseCommand#getToken() }
     * value from the passed in pattern, as well as the help text for the command.
     * 
     * @param pattern the regex pattern for the command
     * @param help a help string to showing how to construct the command
     */
    public ParseCommand( String pattern, String help ) {
        compiledPattern = Pattern.compile( pattern );
        helpString = help;
        token = pattern.charAt( 0 );
    }

    /**
     * Returns the global ElevatorSystem instance used by the parsers
     */
    public static ElevatorSystem getElevatorSystem() {
        return elevatorSystem;
    }

    /**
     * Sets the global ElevatorSystem instance used by the parsers
     */
    public static void setElevatorSystem(ElevatorSystem elevatorSystem) {
        ParseCommand.elevatorSystem = elevatorSystem;
    }

    @Override
    public char getToken() { return token; }
    
    @Override
    public String Parse( String cmd ) {
        return OK;
    }
}
