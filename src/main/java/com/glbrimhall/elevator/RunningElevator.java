/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glbrimhall.elevator;

/**
 *
 * @author geoff
 */
public class RunningElevator {
    public Thread   thread;
    public Elevator elevator;
    
    public RunningElevator()
    {
        this.elevator = new Elevator();
        this.thread = new Thread( this.elevator );
    }
}
