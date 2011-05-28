// 
//  AndroidHardwareMagneticField.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-23.
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

public class AndroidHardwareMagneticField extends AndroidHardwareSensor {
    
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
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (magSensor != null) {
            AndroidHardwareMagneticField waveMagSensor = new AndroidHardwareMagneticField(sensorManager, "uT"); // micro-Tesla
            waveMagSensor.hardwareSensor = magSensor;
            // Always three channels in current Android OS version
            waveMagSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[0]));
            waveMagSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[1]));
            waveMagSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[2]));
            
            set.add(waveMagSensor);
        }
        
        return set;
    }
    
    public AndroidHardwareMagneticField(SensorManager sensorManager, String units) {
        super(sensorManager, "MAGNETIC_FIELD", units);
    }

    public Double getMaximumAvailableSamplingFrequency() {
        return null;
    }
    
    public Double getMaximumAvailablePrecision() {
        // TODO: determine precision
        return null;
    }

    protected Map<String, Double> sensorEventAsValues(SensorEvent event) {
        Map<String, Double> result = new HashMap<String, Double>(3);
        result.put(CHANNEL_NAMES[0], new Double(event.values[0]));
        result.put(CHANNEL_NAMES[1], new Double(event.values[1]));
        result.put(CHANNEL_NAMES[2], new Double(event.values[2]));
        return result;
    }
}