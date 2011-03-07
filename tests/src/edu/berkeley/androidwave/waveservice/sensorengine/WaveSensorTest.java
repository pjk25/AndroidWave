// 
//  WaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveSensor unit test
 * 
 * @see WaveSensor
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorTest extends AndroidTestCase {
    @Override
    protected void setUp() {
        // create fixture sensors
    }
    
    @SmallTest
    public void testGetType() throws Exception {
        fail("test not written yet");
    }
    
    @SmallTest
    public void testGetVersion() throws Exception {
        fail("test not written yet");
    }
    
    @MediumTest
    public void testGetChannels() throws Exception {
        fail("test not written yet");
    }
    
    /**
     * Static Method Tests
     */
    @LargeTest
    public void testGetAllSensors() throws Exception {
        fail("test not written yet");
    }
    
    @LargeTest
    public void testGetAvailableSensors() throws Exception {
        fail("test not written yet");
    }
} 