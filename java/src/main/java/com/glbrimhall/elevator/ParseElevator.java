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
public class ParseElevator extends ParseCommand {

    public ParseElevator() {
        super( "e(\\d+)f([0-9,-]+)", "elevator <num> floorlist: example e2f2,4-6 means elevator 2 floor button 2, 4 through 6 pressed, note elevator is 1 based" );
    }

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

