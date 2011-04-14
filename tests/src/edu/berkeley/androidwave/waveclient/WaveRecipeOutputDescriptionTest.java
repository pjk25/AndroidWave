// 
//  WaveRecipeOutputTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-15.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeOutputTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeOutputTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputTest extends AndroidTestCase {
    
    WaveRecipeOutput anOutput;
    
    public void setUp() {
        anOutput = new WaveRecipeOutput("AccelerometerMagnitude", "g");
    }
    
    public void testName() {
        TestUtils.assertHasMethod("public java.lang.String edu.berkeley.androidwave.waverecipe.WaveRecipeOutput.getName()", anOutput);
        assertEquals("AccelerometerMagnitude", anOutput.getName());
    }
    
    public void testUnits() {
        TestUtils.assertHasMethod("public java.lang.String edu.berkeley.androidwave.waverecipe.WaveRecipeOutput.getUnits()", anOutput);
        assertEquals("g", anOutput.getUnits());
    }
    
    public void testParcelable() {
        fail("test not written yet");
    }
}