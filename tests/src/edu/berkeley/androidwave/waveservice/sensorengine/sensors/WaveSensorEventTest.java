// 
//  WaveSensorEventTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-27.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.test.AndroidTestCase;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * WaveSensorEventTest
 * 
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.sensors.WaveSensorEventTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorEventTest extends AndroidTestCase {
    
    public void testGetValueConformedToPrecision() {
        
        WaveSensorEvent fixtureOne = getFixtureOne(getContext());
        
        double step = 0.01;
        
        assertEquals(1.00, fixtureOne.getValueConformedToPrecision("X", step), step / 100.0);
        assertEquals(0.50, fixtureOne.getValueConformedToPrecision("Y", step), step / 100.0);
        assertEquals(0.02, fixtureOne.getValueConformedToPrecision("Z", step), step / 100.0);
    }
    
    /**
     * FIXTURES
     */
    
    public static WaveSensorEvent getFixtureOne(Context c) {
        AndroidHardwareAccelerometer sensor = AndroidHardwareAccelerometerTest.getFixtureOne(c);
        
        Map<String, Double> values = new HashMap<String, Double>();
        values.put("X", 1.0);
        values.put("Y", 0.5);
        values.put("Z", 0.027);
        
        return new WaveSensorEvent(sensor, System.currentTimeMillis(), values);
    }
}