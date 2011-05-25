// 
//  WaveRecipeOutputDataTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-16.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import android.test.AndroidTestCase;

/**
 * WaveRecipeOutputDataTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeOutputDataTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputDataTest extends AndroidTestCase {
    
    /**
     * testQuantize
     */
    public void testQuantize() throws Exception {
        
        WaveRecipeOutputData fixtureOne = getFixtureOne();
        
        double step = 0.1;
        
        fixtureOne.quantize(step);
        
        // we allow a delta that is 1/100th of the step
        assertEquals(1.0, fixtureOne.getChannelValue("x"), step / 100.0);
        assertEquals(0.5, fixtureOne.getChannelValue("y"), step / 100.0);
        assertEquals(0.0, fixtureOne.getChannelValue("z"), step / 100.0);
    }
    
    public static WaveRecipeOutputData getFixtureOne() {
        WaveRecipeOutputData d = new WaveRecipeOutputData(System.currentTimeMillis());
        
        d.setChannelValue("x", 1.0);
        d.setChannelValue("y", 0.5);
        d.setChannelValue("z", 0.001);
        
        return d;
    }
}