/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

/**
 * This really is meant to be an interface, however for code convienience it's nice
 *
 * @author geoff
 */
interface IParseCommand {
    public char getToken();
    public String Parse( String cmd );
}
