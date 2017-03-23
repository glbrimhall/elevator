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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * The ParseList is the master parsing class running in it's own thread.
 * It maps all user input to sub parsing classes such {@link ParseFloor}, 
 * {@link ParseElevator}.
 */
public class ParseList implements Runnable {
    protected ArrayList<ParseCommand>   parseList = null;
    protected Thread                    thread = null;
    protected Scanner                   input  = null;
    
    public ParseList( ElevatorSystem system ) {
        ParseCommand.setElevatorSystem( system );
       
        input  = new Scanner( System.in );
        parseList = new ArrayList<ParseCommand>();
        
        parseList.add( new ParseQuit() );
        parseList.add( new ParseStatus() );
        parseList.add( new ParseNumberElevatorFloors() );
        parseList.add( new ParseElevator() );
        parseList.add( new ParseFloor() );
        
        thread = new Thread( this );
    }

    /** 
     * Starts the parser's thread
     */
    public void start() { thread.start(); }
    
    /** 
     * Shuts down the parser ( and its thread ). Should be called
     * by the master thread.
     */
    public void stop() throws InterruptedException {
       input.close();
       thread.interrupt();
       thread.join();
    }

    /**
     * Parses user input of commands, mapping it to a particular Parse object
     * @param input string command
     * @return output from the Parser choosen.
     */
    public String Parse( String cmd ) {

        if ( cmd.isEmpty() ) { 
            return "";
        }

        char cmdToken = cmd.charAt(0);
        
        for( ParseCommand command: parseList ) {
            if ( cmdToken == command.getToken() ) {
                return command.Parse( cmd );
            }
        }
        
        return "Unknown command: " + cmd;
    }
    
    @Override
    public void run()
    {
        ElevatorSystem  elevatorSystem = ParseCommand.getElevatorSystem();

        try {
            while( elevatorSystem.isRunning() ) {
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
