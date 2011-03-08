// 
//  WaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.os.Build;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveSensor/WaveSensorChannel test
 * 
 * @see WaveSensor, @see WaveSensorChannel
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorTest extends AndroidTestCase {
    
    protected WaveSensor fakeAccelerometer;
    
    @Override
    protected void setUp() {
        // create fixture sensors
        fakeAccelerometer = new WaveSensor();
        fakeAccelerometer.type = WaveSensor.Type.ACCELEROMETER;
        
        WaveSensorChannel[] channels = { new WaveSensorChannel("X"),
                                         new WaveSensorChannel("Y"),
                                         new WaveSensorChannel("Z")};
        
        fakeAccelerometer.channels = channels;
    }
    
    @SmallTest
    public void testGetType() throws Exception {
        assertEquals("fakeAccelerometer has type ACCELEROMETER", WaveSensor.Type.ACCELEROMETER, fakeAccelerometer.getType());
    }
    
    @SmallTest
    public void testGetVersion() throws Exception {
        
        String device = Build.DEVICE;
        String board = Build.BOARD;
        String model = Build.MODEL;
        
        String expectedVersion = device + "_" + board + "_" + model;
        
        assertEquals(expectedVersion, fakeAccelerometer.getVersion());
    }
    
    @MediumTest
    public void testGetChannels() throws Exception {
        WaveSensorChannel[] channels = fakeAccelerometer.getChannels();
        assertEquals("fakeAccelerometer has 3 channels", 3, channels.length);
        // channels themselves are tested in WaveSensorChannelTest
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