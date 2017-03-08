/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

import static com.glbrimhall.elevator.ParseCommand.OK;
import static com.glbrimhall.elevator.ParseCommand.getElevatorSystem;
import java.util.regex.Matcher;

/**
 *
 * @author geoff
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

            getElevatorSystem().Initialize( numElevators, numFloors );
        }
        return OK;
    }
}
