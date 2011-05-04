// 
//  DiscreetGranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-25.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * DiscreetGranularityTable
 */
public class DiscreetGranularityTable extends GranularityTable {
    
    protected List<TableEntry> entries;
    
    public DiscreetGranularityTable() {
        entries = new ArrayList<TableEntry>();
    }
    
    public List<TableEntry> getEntries() {
        return entries;
    }
    
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    @Override
    public double rateForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception {
        
        // we must search for an entry that matches the given map
        for (TableEntry e : entries) {
            if (e.sensorAttributes.equals(attributes)) {
                return e.outputRate;
            }
        }
        throw new Exception("Supplied attribute set does not belong to this DiscreetGranularityTable");
    }

    /**
     * precisionForSensorPrecisions
     */
    @Override
    public double precisionForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception {
        
        for (TableEntry e : entries) {
            if (e.sensorAttributes.equals(attributes)) {
                return e.outputPrecision;
            }
        }
        throw new Exception("Supplied attribute set does not belong to this DiscreetGranularityTable");
    }
}