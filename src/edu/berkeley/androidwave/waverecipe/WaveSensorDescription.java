// 
//  WaveSensorDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import java.util.Vector;

/**
 * WaveSensorDescription
 *
 * This is a representational sensor object, meaning it does (necessarily)
 * correspond to sensors available on the device.  Its denotes requirements
 * of a sensor that can be met, so may be generic, or very precise, depending
 * on the nature of the algorithm.
 */
public class WaveSensorDescription implements SpecifiesExpectedUnits {
    
    protected WaveSensor.Type type;
    
    protected String expectedUnits;
    
    protected Vector<WaveSensorChannelDescription> channels;
    
    public WaveSensorDescription(WaveSensor.Type t, String expectedUnits) {
        type = t;
        this.expectedUnits = expectedUnits;
        
        channels = new Vector<WaveSensorChannelDescription>();
    }
    
    /**
     * getType
     */
    public WaveSensor.Type getType() {
        return type;
    }
    
    /**
     * hasExpectedUnits
     */
    public boolean hasExpectedUnits() {
        return (expectedUnits != null);
    }
    
    /**
     * getExpectedUnits
     */
    public String getExpectedUnits() {
        return expectedUnits;
    }
    
    /**
     * getChannels
     */
    public WaveSensorChannelDescription[] getChannels() {
        return channels.toArray(new WaveSensorChannelDescription[0]);
    }
    
    /**
     * addChannel
     */
    public boolean addChannel(WaveSensorChannelDescription c) {
        return channels.add(c);
    }
}