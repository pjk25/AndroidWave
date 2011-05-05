// 
//  WaveSensorData.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-02.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import java.util.HashMap;
import java.util.Map;

public class WaveSensorData {
    // hold channel name, data pairs
    protected long time;
    protected Map<String, Double> values;
    
    public WaveSensorData(long time, Map<String, Double> values) {
        this.time = time;
        this.values = values;
    }
    
    public long getTime() {
        return time;
    }
    
    public boolean hasChannelName(String name) {
        return values.containsKey(name);
    }
    
    public double getChannelValue(String name) throws Exception {
        return values.get(name).doubleValue();
    }
}