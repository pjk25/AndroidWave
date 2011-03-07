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

public class WaveSensorChannelDescription implements SpecifiesExpectedUnits {
    
    protected String name;
    protected String expectedUnits;
    
    public WaveSensorChannelDescription(String name) {
        this(name, null);
    }
    
    public WaveSensorChannelDescription(String name, String expectedUnits) {
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