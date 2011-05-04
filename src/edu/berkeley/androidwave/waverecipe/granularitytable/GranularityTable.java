// 
//  GranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import java.util.Set;

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
    public abstract double rateForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception;

    /**
     * precisionForSensorPrecisions
     */
    public abstract double precisionForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception;
}