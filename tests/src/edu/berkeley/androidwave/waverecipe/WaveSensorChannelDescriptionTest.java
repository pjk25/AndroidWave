// 
//  WaveSensorChannelDescriptionTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-04-13.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorChannel;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveSensorChannelDescriptionTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescriptionTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorChannelDescriptionTest extends AndroidTestCase {
    
    WaveSensorChannelDescription wscd;
    
    @Override
    public void setUp() {
        wscd = new WaveSensorChannelDescription("x");
    }
    
    public void testLocalStringRepresentation() {
        // the local string representation allows us to record an authorization
        // in a simple serializable form on this device
        assertEquals("", wscd.localStringRepresentation());
    }
}