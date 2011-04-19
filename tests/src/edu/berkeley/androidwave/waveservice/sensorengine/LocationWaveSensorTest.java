// 
//  LocationWaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-09.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * LocationWaveSensorTest
 * 
 * @see LocationWaveSensor
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.LocationWaveSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class LocationWaveSensorTest extends AndroidTestCase {
    
    LocationWaveSensor lws;
    
    @Override
    public void setUp() {
        lws = new LocationWaveSensor(getContext());
    }
    
    public void testType() {
        assertEquals("type is location", WaveSensorDescription.Type.LOCATION, lws.getType());
    }
}