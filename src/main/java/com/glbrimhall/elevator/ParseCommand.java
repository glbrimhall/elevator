/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import java.util.regex.*;

/**
 * This really is meant to be an interface, however for code convienience it's nice
 *
 * @author geoff
 */
public class ParseCommand implements IParseCommand {
    protected Pattern compiledPattern = null;
    protected String helpString = null;
    protected char token;
    protected static ElevatorSystem elevatorSystem;
    protected static final String OK = "OK";
    
    public ParseCommand( String pattern, String help ) {
        compiledPattern = Pattern.compile( pattern );
        helpString = help;
        token = pattern.charAt( 0 );
        }

    public static ElevatorSystem getElevatorSystem() {
        return elevatorSystem;
    }

    public static void setElevatorSystem(ElevatorSystem elevatorSystem) {
        ParseCommand.elevatorSystem = elevatorSystem;
    }

    public char getToken() { return token; }
    
    public String Parse( String cmd ) {
        return OK;
    }
}
