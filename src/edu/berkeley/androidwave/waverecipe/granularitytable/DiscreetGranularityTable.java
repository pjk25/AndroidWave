// 
//  DiscreetGranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-25.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * DiscreetGranularityTable
 */
public class DiscreetGranularityTable extends GranularityTable {
    
    protected Map< Map<WaveSensorDescription, Double>, Double > rateEntries;
    protected Map< Map<WaveSensorDescription, Double>, Double > precisionEntries;
    
    public DiscreetGranularityTable() {
        rateEntries = new HashMap< Map<WaveSensorDescription, Double>, Double>();
        precisionEntries = new HashMap< Map<WaveSensorDescription, Double>, Double>();
    }
    
    public Map< Map<WaveSensorDescription, Double>, Double > getRateEntries() {
        return rateEntries;
    }
    
    public void setRateEntries(Map< Map<WaveSensorDescription, Double>, Double > s) {
        rateEntries = s;
    }
    
    public Map< Map<WaveSensorDescription, Double>, Double > getPrecisionEntries() {
        return precisionEntries;
    }
    
    public void setPrecisionEntries(Map< Map<WaveSensorDescription, Double>, Double > s) {
        precisionEntries = s;
    }
    
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    @Override
    public double rateForSensorRates(HashMap<WaveSensorDescription, Double> rateMap)
            throws Exception {
        
        if (rateEntries.containsKey(rateMap)) {
            return rateEntries.get(rateMap).doubleValue();
        }
        throw new Exception("Supplied rateMap does not belong to this DiscreetGranularityTable");
    }

    /**
     * precisionForSensorPrecisions
     */
    @Override
    public double precisionForSensorPrecisions(HashMap<WaveSensorDescription, Double> precisionMap)
            throws Exception {
        
        if (precisionEntries.containsKey(precisionMap)) {
            return precisionEntries.get(precisionMap).doubleValue();
        }
        throw new Exception("Supplied precisionMap does not belong to this DiscreetGranularityTable");
    }
}