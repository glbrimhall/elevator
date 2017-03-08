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
public class ParseQuit extends ParseCommand {

    public ParseQuit() {
        super( "quit", "quit the elevatorSystem and shutdown" );
    }

    @Override
    public String Parse( String cmd ) {

        elevatorSystem.Shutdown();

        return OK;
    }
}
