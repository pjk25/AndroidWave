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
    protected double sensorEventQuantizedChannel(SensorEvent event, String channelName, double precision) {
        double v = 0.0;
        if (channelName.equals(CHANNEL_NAMES[0])) {
            v = event.values[0];
        } else if (channelName.equals(CHANNEL_NAMES[1])) {
            v = event.values[1];
        } else if (channelName.equals(CHANNEL_NAMES[2])) {
            v = event.values[2];
        }
        long m = (long) (v / precision);
        return (m * precision);
    }
    
    @Override
    protected Map<String, Double> sensorEventQuantized(SensorEvent event, double precision) {
        HashMap<String, Double> values = new HashMap<String, Double>(3);
        for (int i=0; i<3; i++) {
            long m = (long) (event.values[i] / precision);
            values.put(CHANNEL_NAMES[i], (m * precision));
        }
        return values;
    }
}