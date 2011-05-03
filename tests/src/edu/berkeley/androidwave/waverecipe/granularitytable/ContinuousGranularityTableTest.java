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
import java.util.HashSet;
import java.util.Map;
import junit.framework.TestCase;

/**
 * ContinuousGranularityTableTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.granularitytable.ContinuousGranularityTableTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class ContinuousGranularityTableTest extends TestCase {
    
    public void testRateForSensorAttributes() throws Exception {
        
        ContinuousGranularityTable table = new ContinuousGranularityTable();
        
        // next 4 lines initializes as XML would
        WaveSensorDescription aSensor = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, null);
        
        Map<String, WaveSensorDescription> variableMap = new HashMap<String, WaveSensorDescription>();
        variableMap.put("input_accel", aSensor);
        
        table.setRateFormulaString("#input_accel");
        table.setVariableMap(variableMap);
        
        // construct method input
        HashSet<SensorAttributes> attributeSet = new HashSet<SensorAttributes>();
        SensorAttributes sa = new SensorAttributes();
        sa.sensorDescription = aSensor;
        sa.rate = 5.0;
        attributeSet.add(sa);
        
        assertEquals(5.0, table.rateForSensorAttributes(attributeSet));

        sa.rate = 10.0;
        assertEquals(10.0, table.rateForSensorAttributes(attributeSet));
    }
    
    public void testPrecisionForSensorAttributes() throws Exception {
        
        ContinuousGranularityTable table = new ContinuousGranularityTable();
        
        // next 4 lines initializes as XML would
        WaveSensorDescription aSensor = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, null);
        
        Map<String, WaveSensorDescription> variableMap = new HashMap<String, WaveSensorDescription>();
        variableMap.put("input_accel", aSensor);
        
        table.setPrecisionFormulaString("#input_accel");
        table.setVariableMap(variableMap);
        
        // construct method input
        HashSet<SensorAttributes> attributeSet = new HashSet<SensorAttributes>();
        SensorAttributes sa = new SensorAttributes();
        sa.sensorDescription = aSensor;
        sa.precision = 6.0;
        attributeSet.add(sa);

        assertEquals(6.0, table.precisionForSensorAttributes(attributeSet));

        sa.precision = 8.0;
        assertEquals(8.0, table.precisionForSensorAttributes(attributeSet));
    }
}