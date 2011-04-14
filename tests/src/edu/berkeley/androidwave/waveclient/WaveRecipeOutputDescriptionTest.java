// 
//  WaveRecipeOutputDescriptionTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-15.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.TestUtils;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeOutputDescriptionTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeOutputTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputDescriptionTest extends AndroidTestCase {
    
    WaveRecipeOutputDescription anOutput;
    
    public void setUp() {
        anOutput = new WaveRecipeOutputDescription("AccelerometerMagnitude", "g");
    }
    
    public void testName() {
        assertEquals("AccelerometerMagnitude", anOutput.getName());
    }
    
    public void testUnits() {
        assertEquals("g", anOutput.getUnits());
    }
    
    public void testParcelable() {
        fail("test not written yet");
    }
}