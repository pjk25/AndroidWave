// 
//  WaveSensorChannel.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

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