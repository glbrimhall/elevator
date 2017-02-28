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
public enum Movement {
   /**
     * Movement enum provides high level control info on state of the elevator
     * UP - elevator is moving up
     * DOWN - elevator is moving down
     * ATFLOOR - elevator is stopped waiting at a floor
     * MAINTENENCE - elevator is offline
     */
    UP, DOWN;
}
