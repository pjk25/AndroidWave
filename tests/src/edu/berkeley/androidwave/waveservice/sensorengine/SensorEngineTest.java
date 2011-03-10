// 
//  SensorEngineTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.Set;

/**
 * SensorEngineTest
 * 
 * @see SensorEngine
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.SensorEngineTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class SensorEngineTest extends AndroidTestCase {
    
    SensorEngine sensorEngineInstance;
    
    public void setUp() throws Exception {
        SensorEngine.init(getContext());
        sensorEngineInstance = SensorEngine.getInstance();
    }
    
    @MediumTest
    public void testAvailableSensorsMatchingWaveSensorDescription() throws Exception {
        // we will test a sensorDescription that does not specify channels,
        // like the AccelerometerMagnitudeRecipe, to test imprecise matching
        WaveSensorDescription sensorDescription = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, "-m/s^2");
        
        Set<WaveSensor> matchingSensorSet = sensorEngineInstance.availableSensorsMatchingWaveSensorDescription(sensorDescription);
        
        assertEquals("there should be 1 matching sensor", 1, matchingSensorSet.size());
        WaveSensor theMatchingSensor = matchingSensorSet.iterator().next();
        
        assertEquals("and it should be an accelerometer", WaveSensor.Type.ACCELEROMETER, theMatchingSensor.getType());
    }
}