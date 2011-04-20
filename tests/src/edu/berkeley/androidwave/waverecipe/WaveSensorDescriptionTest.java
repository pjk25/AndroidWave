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
    
    WaveSensorDescription wsd;
    
    @Override
    public void setUp() {
        wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "m/s^2");
    }
    
    public void testNewWaveSensorDescriptionHasNoChannels() {
        assertFalse(wsd.hasChannels());
    }
    
    public void testWaveSensorDescriptionGetChannelsNotNull() {
        assertNotNull(wsd.getChannels());
    }
    
    public void testHasChannels() {
        assertFalse(wsd.hasChannels());
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        assertTrue(wsd.hasChannels());
    }
    
    public void testExpectedUnits() {
        // fixture specific
        assertTrue(wsd.hasExpectedUnits());
        assertNotNull(wsd.getExpectedUnits());
        
        // not fixture specific
        WaveSensorDescription unitlessWsd = new WaveSensorDescription(WaveSensorDescription.Type.MAGNETOMETER, null);
        assertFalse(unitlessWsd.hasExpectedUnits());
        assertNull(unitlessWsd.getExpectedUnits());
    }
    
    public void testGetType() {
        assertEquals(WaveSensorDescription.Type.ACCELEROMETER, wsd.getType());
    }
    
    public void testLocalStringRepresentation() {
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        wsd.addChannel(new WaveSensorChannelDescription("y"));
        
        assertEquals("{\"type\":\"ACCELEROMETER\",\"channels\":[\"x\",\"y\"],\"expectedUnits\":\"m\\/s^2\"}", wsd.localStringRepresentation());
    }
}