// 
//  GranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import java.util.HashMap;

/**
 * GranularityTable
 */
public abstract class GranularityTable {
    
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    public abstract double rateForSensorRates(HashMap<WaveSensorDescription, Double> rateMap)
            throws Exception;

    /**
     * precisionForSensorPrecisions
     */
    public abstract double precisionForSensorPrecisions(HashMap<WaveSensorDescription, Double> precisionMap)
            throws Exception;
}