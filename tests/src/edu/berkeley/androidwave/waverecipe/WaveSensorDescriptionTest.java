// 
//  WaveSensorDescriptionTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-09.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveSensorDescriptionTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveSensorDescriptionTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorDescriptionTest extends AndroidTestCase {
    
    public void testNewWaveSensorDescriptionHasNoChannels() {
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, "m/s^2");
        
        assertFalse(wsd.hasChannels());
    }
    
    public void testWaveSensorDescriptionGetChannelsNotNull() {
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, "m/s^2");
        
        assertNotNull(wsd.getChannels());
    }
}