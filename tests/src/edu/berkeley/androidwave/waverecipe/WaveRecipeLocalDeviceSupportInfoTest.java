// 
//  WaveRecipeLocalDeviceSupportInfoTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
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
    public void testNewInstanceIsSupportedReturnsFalse() {
        WaveRecipeLocalDeviceSupportInfo newInfo = new WaveRecipeLocalDeviceSupportInfo();
        assertFalse(newInfo.isSupported());
    }
    
    /**
     * testDescriptionMapsInitialState
     * 
     * test the getter methods for the HashMaps linking sensor descriptions
     * used by the recipe to their maximum available rates and precisions
     */
    @SmallTest
    public void testDescriptionMapsInitialState() {
        WaveRecipeLocalDeviceSupportInfo info = new WaveRecipeLocalDeviceSupportInfo();
        
        HashMap descriptionMap;
        // sensorDescriptionMaxRateMap
        descriptionMap = info.getSensorDescriptionMaxRateMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorDescriptionMaxRateMap size", 0, descriptionMap.size());

        // sensorDescriptionMaxPrecisionMap
        descriptionMap = info.getSensorDescriptionMaxPrecisionMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorDescriptionMaxPrecisionMap size", 0, descriptionMap.size());

        // sensorChannelDescriptionMaxRateMap
        descriptionMap = info.getSensorChannelDescriptionMaxRateMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorChannelDescriptionMaxRateMap size", 0, descriptionMap.size());

        // sensorChannelDescriptionMaxPrecisionMap
        descriptionMap = info.getSensorChannelDescriptionMaxPrecisionMap();
        assertNotNull(descriptionMap);
        assertEquals("sensorChannelDescriptionMaxPrecisionMap size", 0, descriptionMap.size());
    }
}