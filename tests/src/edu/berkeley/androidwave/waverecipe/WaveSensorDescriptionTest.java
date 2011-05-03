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
    
    public void testPreconditions() {
        WaveSensorDescription one = getFixtureOne();
        WaveSensorDescription two = getFixtureTwo();
        WaveSensorDescription likeOne = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "g");
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, one, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, likeOne, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
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
    
    /**
     * FIXTURES
     */
    
    public static WaveSensorDescription getFixtureOne() {
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "g");
        return wsd;
    }
    
    public static WaveSensorDescription getFixtureTwo() {
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "m/s^2");
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        wsd.addChannel(new WaveSensorChannelDescription("y"));
        wsd.addChannel(new WaveSensorChannelDescription("z"));
        return wsd;
    }
    
    public static WaveSensorDescription getFixtureThree() {
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.MAGNETOMETER, "uT");
        return wsd;
    }
}