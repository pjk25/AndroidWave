// 
//  WaveSensorChannelTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.test.AndroidTestCase;

/**
 * WaveSensorChannelTest
 * 
 * unit test for @see WaveSensorChannel
 * 
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.sensors.WaveSensorChannelTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorChannelTest extends AndroidTestCase {
    
    WaveSensorChannel xChannel;
    
    public void setUp() {
        xChannel = new WaveSensorChannel("X");
    }
    
    public void testGetName() {
        assertEquals("X", xChannel.getName());
    }
}