// 
//  AndroidHardwareAccelerometer.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AndroidHardwareAccelerometer extends AndroidHardwareSensor {
    
    private static final String[] CHANNEL_NAMES = new String[] {"x", "y", "z"};
    
    /**
     * instancesAvailableInContext
     * 
     * Static method, returning a Set of WaveSensor instances representing
     * distinct physical sensors available in the given Context
     * 
     * The default implementation returns an empty set, as the abstract
     * WaveSensor class will never correspond to any sensors (but subclasses
     * may, and should)
     */
    public static Set<WaveSensor> instancesAvailableInContext(Context c) {
        
        Set<WaveSensor> set = new HashSet<WaveSensor>(1);
        
        SensorManager sensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        if (accelSensor != null) {
            // we have an accelerometer
            AndroidHardwareAccelerometer waveAccelSensor = new AndroidHardwareAccelerometer(sensorManager, "-m/s^2");
            waveAccelSensor.hardwareSensor = accelSensor;
            // It will always have three channels in the current version
            // of the Android OS
            waveAccelSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[0]));
            waveAccelSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[1]));
            waveAccelSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[2]));
            
            set.add(waveAccelSensor);
        }
        
        return set;
    }
    
    public AndroidHardwareAccelerometer(SensorManager sensorManager, String units) {
        super(sensorManager, "ACCELEROMETER", units);
    }

    @Override
    public Double getMaximumAvailableSamplingFrequency() {
        return null;
    }
    
    @Override
    public Double getMaximumAvailablePrecision() {
        // assume sensor reports binary thousands of a g
        return (9.81/1024.0);
    }
    
    @Override
    protected Map<String, Double> sensorEventAsValues(SensorEvent event) {
        Map<String, Double> result = new HashMap<String, Double>(3);
        result.put(CHANNEL_NAMES[0], new Double(event.values[0]));
        result.put(CHANNEL_NAMES[1], new Double(event.values[1]));
        result.put(CHANNEL_NAMES[2], new Double(event.values[2]));
        return result;
    }
}