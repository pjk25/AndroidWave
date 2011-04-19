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

public class DiscreetGranularityTable extends GranularityTable {
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    @Override
    public double rateForSensorRates(HashMap<WaveSensorDescription, Double> rateMap)
            throws Exception {
        throw new Exception("DiscreetGranularityTable#rateForSensorRates not implemented yet!");
    }

    /**
     * precisionForSensorPrecisions
     */
    @Override
    public double precisionForSensorPrecisions(HashMap<WaveSensorDescription, Double> precisionMap)
            throws Exception {
        throw new Exception("DiscreetGranularityTable#precisionForSensorPrecisions not implemented yet!");
    }
}