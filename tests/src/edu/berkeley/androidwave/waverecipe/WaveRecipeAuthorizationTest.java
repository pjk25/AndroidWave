// 
//  WaveRecipeAuthorizationTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-02-04.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveservice.sensorengine.SensorEngine;

import java.io.*;
import java.util.HashMap;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeAuthorizationTest
 * 
 * Unit test for the WaveRecipeAuthorization class
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorizationTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeAuthorizationTest extends AndroidTestCase {
    
    SensorEngine sensorEngine;
    WaveRecipe recipeOne;
    
    protected void setUp() throws Exception {
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        recipeOne = WaveRecipe.createFromDisk(getContext(), targetFile.getPath());
        
        SensorEngine.init(getContext());
        sensorEngine = SensorEngine.getInstance();
    }
    
    /**
     * ensure that a WaveRecipeAuthorization can be constructed with a
     * {@code WaveRecipeLocalDeviceSupportInfo} argument
     */
    @SmallTest
    public void testConstructor() throws Exception {
        
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngine.supportInfoForRecipe(recipeOne);
        assertTrue(supportInfo.isSupported());
        
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(supportInfo);
        assertNotNull(auth);
    }
    
    
    /**
     * testDescriptionMapsInitialState
     * 
     * test the getter methods for the HashMaps linking sensor descriptions
     * used by the recipe to their maximum available rates and precisions
     */
    @SmallTest
    public void testDescriptionMapsInitialState() throws Exception {
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngine.supportInfoForRecipe(recipeOne);
        assertTrue(supportInfo.isSupported());
        
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(supportInfo);
        
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
    
    /**
     * test the asInfo method, which produces a WaveRecipeAuthorizationInfo
     * object.
     */
    @SmallTest
    public void testAsInfo() {
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngine.supportInfoForRecipe(recipeOne);
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(supportInfo);
        
        WaveRecipeAuthorizationInfo info = auth.asInfo();
        assertNotNull(info);
        
        fail("test not finished yet");
    }
}