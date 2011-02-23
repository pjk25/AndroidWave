// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

/**
 * WaveSensor
 *
 * This is a representational sensor object, meaning it does (necessarily)
 * correspond to sensors available on the device.  Its denotes requirements
 * of a sensor that can be met, so may be generic, or very precise, depending
 * on the nature of the algorithm.
 */
public class WaveSensor {
    
    public enum Type { ACCELEROMETER };
    
    protected Type type;
    
    public WaveSensor(Type t) {
        type = t;
    }
    
    /**
     * getType
     */
    public Type getType() {
        return type;
    }
}