/*
 * Copyright (C) 2017 glbrimhall.com
 *
 * This file is part of an Elevator Simulator program written in java.
 *
 * Elevator Simulator program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License.
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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 *
 * @author geoff
 */
public class ParseList implements Runnable {
    protected ArrayList<ParseCommand>  parseList = null;
    protected ElevatorSystem           elevatorSystem = null;
    
    public ParseList( ElevatorSystem system ) {
        elevatorSystem = system;

        parseList = new ArrayList<ParseCommand>();
        
        parseList.add( new ParseQuit() );
        parseList.add( new ParseStatus() );
        parseList.add( new ParseNumberElevatorFloors() );
        parseList.add( new ParseElevator() );
        parseList.add( new ParseFloor() );        
    }
    
    public String Parse( String input ) {

        if ( input.isEmpty() ) { 
            return "";
        }

        char inputToken = input.charAt(0);
        
        for( ParseCommand command: parseList ) {
            if ( inputToken == command.getToken() ) {
                return command.Parse( input );
            }
        }
        
        return "Unknown command: " + input;
    }
    
    @Override
    public void run()
    {
        Scanner input = elevatorSystem.getInput();

        try {
            while( elevatorSystem.isRunning() )
            {
                if ( input.hasNextLine() ) {
                    String result = Parse( input.nextLine() );
                    System.out.println( result );
                }
            }
        }
        catch (IllegalStateException ex) {
        }
        finally {
            input.close();
        }
    }
}
