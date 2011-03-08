// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.os.Build;

public class WaveSensor {
    protected final String BASE_VERSION = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    public enum Type { ACCELEROMETER };
    
    protected Type type;
    
    protected WaveSensorChannel[] channels;
    
    /**
     * getType
     */
    public Type getType() {
        return type;
    }
    
    /**
     * getVersion
     *
     * should allow identification of sensor hardware
     */
    public String getVersion() {
        return BASE_VERSION;
    }
    
    /**
     * getChannels
     * 
     * @see WaveSensorChannel
     */
    public WaveSensorChannel[] getChannels() {
        return channels;
    }
}