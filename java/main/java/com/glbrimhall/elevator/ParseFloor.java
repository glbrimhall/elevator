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
public class ParseFloor extends ParseCommand {

    public ParseFloor() {
        super( "f(\\d+)(u|d)+", "floor <num> up or down: example f3u means floor 3 up" );
    }

    @Override
    public String Parse( String cmd ) {
        Matcher m = compiledPattern.matcher( cmd );

        while( m.find() ) {
            int         floor = Integer.parseInt( m.group( 1 ) );
            String      upordown = m.group( 2 );
            Movement    direction = Movement.UP;
            
            // Note we default to going up !
            if ( upordown.charAt( 0 ) == 'd' ) {
                direction = Movement.DOWN;
            }
            getElevatorSystem().floorRequest( floor, direction );
        }
        return OK;
    }
}
