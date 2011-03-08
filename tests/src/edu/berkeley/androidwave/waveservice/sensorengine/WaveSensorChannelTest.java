// 
//  WaveSensorChannelTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.test.AndroidTestCase;

/**
 * WaveSensorChannelTest
 * 
 * unit test for @see WaveSensorChannel
 * 
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorChannelTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorChannelTest extends AndroidTestCase {
    
    WaveSensorChannel xChannel;
    
    public void setUp() {
        xChannel = new WaveSensorChannel("X");
        xChannel.expectedUnits = "g";
    }
    
    public void testGetName() {
        assertEquals("X", xChannel.getName());
    }
    
    public void testGetExpectedUnits() {
        assertEquals("g", xChannel.getExpectedUnits());
    }
}