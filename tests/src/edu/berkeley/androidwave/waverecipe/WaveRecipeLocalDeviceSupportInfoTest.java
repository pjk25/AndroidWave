// 
//  WaveRecipeLocalDeviceSupportInfoTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;

import android.test.InstrumentationTestCase;
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
public class WaveRecipeLocalDeviceSupportInfoTest extends InstrumentationTestCase {
    
    /**
     * testNewInstanceIsSupportedReturnsFalse
     * 
     * A newly created instance should not indicate support in it's default
     * state
     */
    @SmallTest
    public void testNewInstanceIsSupportedReturnsFalse() throws Exception {
        File targetFile = TestUtils.copyAssetToInternal(getInstrumentation(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipeOne = WaveRecipe.createFromDisk(getInstrumentation().getContext(), targetFile.getPath());
        
        WaveRecipeLocalDeviceSupportInfo newInfo = new WaveRecipeLocalDeviceSupportInfo(recipeOne);
        assertFalse(newInfo.isSupported());
    }
}