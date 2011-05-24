// 
//  WaveSensorChannel.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

/**
 * WaveSensorChannel
 * 
 * @see WaveSensor
 */
public class WaveSensorChannel {
    
    protected String name;
    
    /**
     * WaveSensorChannel
     * 
     * Constructor
     */
    public WaveSensorChannel(String name) {
        this.name = name;
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
    }
}