// 
//  AndroidWaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.hardware.Sensor;

/**
 * AndroidWaveSensor
 * 
 * Subclass of {@link WaveSensor} specialized for sensors backed by an Android
 * OS sensor
 */
public class AndroidWaveSensor extends WaveSensor {
    
    protected Sensor androidSensor;
    
    /**
     * WaveSensor
     */
    public AndroidWaveSensor(WaveSensor.Type t) {
        super(t);
    }

    /**
     * getVersion
     *
     * should allow identification of sensor hardware
     */
    public String getVersion() {
        // TODO: initialize androidSensor in tests
        String name = androidSensor.getName();
        String version = "" + androidSensor.getVersion();
        return BASE_VERSION + "_" + name + "_" + version;
    }
}