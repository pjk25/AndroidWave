// 
//  AndroidWaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

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
    public AndroidWaveSensor(WaveSensorDescription.Type t, String units) {
        super(t, units);
    }

    /**
     * getVersion
     *
     * should allow identification of sensor hardware
     */
    public String getVersion() {
        String name = androidSensor.getName();
        String version = "" + androidSensor.getVersion();
        return VERSION_BASE + "_" + WaveSensorDescription.typeToString(type) + "_" + name + "_" + version;
    }
    
    public Sensor getAndroidSensor() {
        return androidSensor;
    }
}