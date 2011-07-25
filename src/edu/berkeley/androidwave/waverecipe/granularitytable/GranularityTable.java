// 
//  GranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.SensorAttributes;

import java.util.Set;

/**
 * GranularityTable
 * 
 * In memory representation of a mapping between input and output
 * granularities provided in a recipe's XML.
 */
public abstract class GranularityTable {
    
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    public abstract double rateForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception;

    /**
     * precisionForSensorPrecisions
     */
    public abstract double precisionForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception;
}