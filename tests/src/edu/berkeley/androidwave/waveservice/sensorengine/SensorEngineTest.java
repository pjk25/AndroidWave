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
 * 
 */
public class SensorEngineTest extends AndroidTestCase {
    
    @MediumTest
    public void testAvailableSensorsMatchingWaveSensorDescription() {
        // we will test a sensorDescription that does not specify channels,
        // like the AccelerometerMagnitudeRecipe, to test imprecise matching
        WaveSensorDescription sensorDescription = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, "m/s^2");
        
        Set<WaveSensor> matchingSensorSet = SensorEngine.availableSensorsMatchingWaveSensorDescription(sensorDescription);
        
        assertEquals("there should be 1 matching sensor", 1, matchingSensorSet.size());
        WaveSensor theMatchingSensor = matchingSensorSet.iterator().next();
        
        assertEquals("and it should be an accelerometer", WaveSensor.Type.ACCELEROMETER, theMatchingSensor.getType());
    }
}