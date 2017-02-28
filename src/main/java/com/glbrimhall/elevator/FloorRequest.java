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
public class FloorRequest {
    public int      floor;
    public Movement direction;

    public FloorRequest(int floor, Movement direction) {
        this.floor = floor;
        this.direction = direction;
    }
}
