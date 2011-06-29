// 
//  WaveRecipeLocalDeviceSupportInfoTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.util.HashMap;

/**
 * WaveRecipeLocalDeviceSupportInfoTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeLocalDeviceSupportInfoTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeLocalDeviceSupportInfoTest extends AndroidTestCase {
    
    /**
     * testNewInstanceIsSupportedReturnsFalse
     * 
     * A newly created instance should not indicate support in it's default
     * state
     */
    @SmallTest
    public void testNewInstanceIsSupportedReturnsFalse() throws Exception {
        
        WaveRecipe recipeOne = WaveRecipeTest.getFixtureOne(getContext());
        
        WaveRecipeLocalDeviceSupportInfo newInfo = new WaveRecipeLocalDeviceSupportInfo(recipeOne);
        assertFalse(newInfo.isSupported());
    }
}