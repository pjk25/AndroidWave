// 
//  WaveRecipeAuthorizationTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-02-04.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;

import java.io.*;
import android.test.InstrumentationTestCase;

/**
 * WaveRecipeAuthorizationTest
 * 
 * Unit test for the WaveRecipeAuthorization class
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorizationTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeAuthorizationTest extends InstrumentationTestCase {
    
    protected void setUp() {
        
    }
    
    public void testConstructor() throws Exception {
        File targetFile = TestUtils.copyAssetToInternal(getInstrumentation(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipe = WaveRecipe.createFromDisk(getInstrumentation().getContext(), targetFile.getPath());
        
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(recipe);
        assertNotNull(auth);
    }
    
    
    public void testParcelable() throws Exception {
        fail("test not writen yet");
    }
}