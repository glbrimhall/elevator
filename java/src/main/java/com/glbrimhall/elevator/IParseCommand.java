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

/**
 * The IParseCommand defines the parsing interface, which is provides a 
 * first unique char token for each command and a subsequent Parse method.
 */
interface IParseCommand {

    /** 
     * Returns the first character token of the parse command, to allow
     * quickly mapping an input command to it's parsing object.
     */
    public char getToken();

    /** 
     * Parse an input command mapping to a functional call in the ElevatorSystem.
     * 
     * @param cmd the string command
     * @return "OK" indicating successful parsing, or an error 
     * message while parsing.
     */
    public String Parse( String cmd );
}
