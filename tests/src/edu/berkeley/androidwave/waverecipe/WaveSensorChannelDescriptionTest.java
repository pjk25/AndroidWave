// 
//  WaveSensorChannelDescriptionTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-04-13.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
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
    
    public void testPreconditions() {
        WaveSensorChannelDescription one = getFixtureOne();
        WaveSensorChannelDescription two = getFixtureTwo();
        WaveSensorChannelDescription likeOne = new WaveSensorChannelDescription("x");
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, one, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, likeOne, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
    }
    
    /**
     * FIXTURES
     */
    
    public static WaveSensorChannelDescription getFixtureOne() {
        return new WaveSensorChannelDescription("x");
    }
    
    public static WaveSensorChannelDescription getFixtureTwo() {
        return new WaveSensorChannelDescription("y");
    }
}