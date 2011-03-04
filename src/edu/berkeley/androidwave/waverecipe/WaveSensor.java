// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import java.util.Vector;

/**
 * WaveSensor
 *
 * This is a representational sensor object, meaning it does (necessarily)
 * correspond to sensors available on the device.  Its denotes requirements
 * of a sensor that can be met, so may be generic, or very precise, depending
 * on the nature of the algorithm.
 */
public class WaveSensor implements SpecifiesExpectedUnits {
    
    public enum Type { ACCELEROMETER };
    
    protected Type type;
    
    protected String expectedUnits;
    
    protected Vector<WaveSensorChannel> channels;
    
    public WaveSensor(Type t, String expectedUnits) {
        type = t;
        this.expectedUnits = expectedUnits;
        
        channels = new Vector<WaveSensorChannel>();
    }
    
    /**
     * getType
     */
    public Type getType() {
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
    public WaveSensorChannel[] getChannels() {
        return channels.toArray(new WaveSensorChannel[0]);
    }
    
    /**
     * addChannel
     */
    public boolean addChannel(WaveSensorChannel c) {
        return channels.add(c);
    }
}