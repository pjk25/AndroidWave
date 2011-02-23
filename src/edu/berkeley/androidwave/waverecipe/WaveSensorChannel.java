// 
//  WaveSensorChannel.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

/**
 * WaveSensorChannel
 *
 * @see WaveSensor
 */

public class WaveSensorChannel {
    
    protected String name;
    
    public WaveSensorChannel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}