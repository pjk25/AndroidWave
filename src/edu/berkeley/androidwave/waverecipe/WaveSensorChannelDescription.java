// 
//  WaveSensorChannelDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

/**
 * WaveSensorChannelDescription
 *
 * @see WaveSensor
 */

public class WaveSensorChannelDescription {
    
    protected String name;
    
    public WaveSensorChannelDescription(String name) {
        this.name = name;
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
    }
    
    /**
     * localStringRepresentation
     */
    protected String localStringRepresentation() {
        return null;
    }
}