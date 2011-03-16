// 
//  ContinuousGranularityTableTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-02-28.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import java.util.HashMap;
import junit.framework.TestCase;

/**
 * ContinuousGranularityTableTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.granularitytable.ContinuousGranularityTableTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class ContinuousGranularityTableTest extends TestCase {
    
    public void testRateForSensorRates() throws Exception {
        
        ContinuousGranularityTable table = new ContinuousGranularityTable();
        
        // next 4 lines initializes as XML would
        WaveSensorDescription aSensor = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, null);
        
        HashMap<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("input_accel", aSensor);
        
        table.setRateFormulaString("#input_accel");
        table.setVariableMap(variableMap);
        
        // construct method input
        HashMap<Object, Double> rateMap;
        
        rateMap = new HashMap<Object, Double>();
        rateMap.put(aSensor, 5.0);
        assertEquals(5.0, table.rateForSensorRates(rateMap));

        rateMap = new HashMap<Object, Double>();
        rateMap.put(aSensor, 10.0);
        assertEquals(10.0, table.rateForSensorRates(rateMap));
    }
    
    public void testPrecisionForSensorPrecisions() throws Exception {
        
        ContinuousGranularityTable table = new ContinuousGranularityTable();
        
        // next 4 lines initializes as XML would
        WaveSensorDescription aSensor = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, null);
        
        HashMap<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("input_accel", aSensor);
        
        table.setRateFormulaString("#input_accel");
        table.setVariableMap(variableMap);
        
        // construct method input
        HashMap<Object, Double> rateMap;
        
        rateMap = new HashMap<Object, Double>();
        rateMap.put(aSensor, 6.0);
        assertEquals(6.0, table.rateForSensorRates(rateMap));

        rateMap = new HashMap<Object, Double>();
        rateMap.put(aSensor, 8.0);
        assertEquals(8.0, table.rateForSensorRates(rateMap));
    }
}