// 
//  AndroidLocationSensorEvent.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-06-01.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import java.util.Map;

/**
 * Special subclass of WaveSensorEvent for the AndroidLocationSensor which
 * provides special handling of adjusting precisions
 */
public class AndroidLocationSensorEvent extends WaveSensorEvent {
    
    public AndroidLocationSensorEvent(AndroidLocationSensor sensor, long timestamp, Map<String, Double> values) {
        super(sensor, timestamp, values);
    }
    
    @Override
    public double getValueConformedToPrecision(String name, double step) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}