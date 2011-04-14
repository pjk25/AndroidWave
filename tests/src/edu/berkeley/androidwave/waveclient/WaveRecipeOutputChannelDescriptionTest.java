// 
//  WaveRecipeOutputChannelTest.java
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
 * WaveRecipeOutputChannelTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeOutputChannelTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputChannelTest extends AndroidTestCase {
    
    WaveRecipeOutputChannel anOutputChannel;
    
    public void setUp() {
        anOutputChannel = new WaveRecipeOutputChannel("magnitude");
    }
    
    public void testName() {
        TestUtils.assertHasMethod("public java.lang.String edu.berkeley.androidwave.waverecipe.WaveRecipeOutputChannel.getName()", anOutputChannel);
        assertEquals("magnitude", anOutputChannel.getName());
    }
    
    public void testParcelable() {
        fail("test not written yet");
    }
}