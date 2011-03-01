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

public class WaveSensorChannel implements SpecifiesExpectedUnits {
    
    protected String name;
    protected String expectedUnits;
    
    public WaveSensorChannel(String name) {
        this(name, null);
    }
    
    public WaveSensorChannel(String name, String expectedUnits) {
        this.name = name;
        this.expectedUnits = expectedUnits;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean hasExpectedUnits() {
        return (expectedUnits != null);
    }
    
    public String getExpectedUnits() {
        return expectedUnits;
    }
}