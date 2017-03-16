package com.glbrimhall.elevator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
