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
import java.util.HashMap;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeAuthorizationTest
 * 
 * Unit test for the WaveRecipeAuthorization class
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorizationTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeAuthorizationTest extends InstrumentationTestCase {
    
    WaveRecipe recipeOne;
    
    protected void setUp() throws Exception {
        File targetFile = TestUtils.copyAssetToInternal(getInstrumentation(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        recipeOne = WaveRecipe.createFromDisk(getInstrumentation().getContext(), targetFile.getPath());
    }
    
    /**
     * ensure that a WaveRecipeAuthorization can be constructed with a
     * {@code WaveRecipe} as a single argument
     */
    @SmallTest
    public void testConstructor() throws Exception {
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(recipeOne);
        assertNotNull(auth);
    }
    
    
    /**
     * testDescriptionMapsInitialState
     * 
     * test the getter methods for the HashMaps linking sensor descriptions
     * used by the recipe to their maximum available rates and precisions
     */
    @SmallTest
    public void testDescriptionMapsInitialState() {
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(recipeOne);
        
        HashMap descriptionMap;
        // sensorDescriptionMaxRateMap
        descriptionMap = auth.getSensorDescriptionMaxRateMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorDescriptionMaxRateMap size", 0, descriptionMap.size());

        // sensorDescriptionMaxPrecisionMap
        descriptionMap = auth.getSensorDescriptionMaxPrecisionMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorDescriptionMaxPrecisionMap size", 0, descriptionMap.size());

        // sensorChannelDescriptionMaxRateMap
        descriptionMap = auth.getSensorChannelDescriptionMaxRateMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorChannelDescriptionMaxRateMap size", 0, descriptionMap.size());

        // sensorChannelDescriptionMaxPrecisionMap
        descriptionMap = auth.getSensorChannelDescriptionMaxPrecisionMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorChannelDescriptionMaxPrecisionMap size", 0, descriptionMap.size());
    }

    public void testParcelable() throws Exception {
        fail("test not writen yet");
    }
}