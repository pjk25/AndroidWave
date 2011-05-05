// 
//  WaveRecipeOutputData.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * WaveRecipeOutputData
 *
 * A simplified interface representing WaveRecipeOutput as relevant to
 * WaveRecipes.
 */
public class WaveRecipeOutputData {
    protected long time;
    protected Map<String, Double> values;
    
    public WaveRecipeOutputData(long time, Map<String, Double> values) {
        this.time = time;
        this.values = values;
    }
    
    public long getTime() {
        return time;
    }
    
    public Map<String, Double> getValues() {
        return values;
    }
    
    public boolean hasChannelName(String name) {
        return values.containsKey(name);
    }
    
    public double getChannelValue(String name) throws Exception {
        return values.get(name).doubleValue();
    }
    
    public void setChannelValue(String name, double value) {
        values.put(name, value);
    }
    
    public void quantize(double s) {
        //Map<String, Double> newValues = new HashMap<String, Double>();
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            double v = entry.getValue().doubleValue();
            
            long factor = (long) (v / s);
            v = ((double)factor) * s;
            
            // try this
            values.put(entry.getKey(), v);
        }
    }
}