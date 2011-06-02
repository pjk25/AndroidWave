// 
//  WaveSensorEvent.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-27.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import java.util.HashMap;
import java.util.Map;

/**
 * WaveSensorEvent
 */
public class WaveSensorEvent {
    
    public WaveSensor sensor;
    
    public long timestamp;
    
    public Map<String, Double> values;
    
    public WaveSensorEvent(WaveSensor sensor, long timestamp, Map<String, Double> values) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.values = values;
    }
    
    /**
     * getValueConformedToPrecision
     * 
     * quantize the sensed values by the increment step
     */
    public double getValueConformedToPrecision(String name, double step) {
        double v = values.get(name).doubleValue();
        long factor = (long) (v / step);
        return ((double)factor) * step;
    }
}