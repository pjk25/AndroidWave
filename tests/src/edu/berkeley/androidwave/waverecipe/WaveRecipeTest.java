// 
//  WaveRecipeTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import android.test.InstrumentationTestCase;
//import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeTest
 * 
 * WaveRecipe is the in memory representation of the transferable Recipe
 * object from the Recipe authority.
 * It should have - a unique identifier
 *                  a version
 *                  a granularity table
 *                  an algorithm representation
 *                  needed sensors
 *                  generated outputs
 * 
 * We use an InstrumentationTestCase so we can use fixtures from the test
 * app's assets
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.WaveRecipeTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeTest extends InstrumentationTestCase {
    
    protected void setUp() {}
    
    /**
     * testRetrieveRecipe
     *
     * Retrieve a recipe from a recipe authority.  Note that we need a
     * running recipe server for this.
     */
    public void testCreateFromID() {
        Assert.fail("test not written yet");
        
        String recipeID = "";
        int version = 0;
        
        WaveRecipe testRecipe = WaveRecipe.createFromID(recipeID, version);
        
        Assert.assertNotNull(testRecipe);
    }
    
    /**
     * testCreateFromDisk
     * 
     * Make sure than we can construct a recipe from it's on disk
     * representation.  Superceeded by {@link labeltestRetrieveRecipe}
     */
    public void testCreateFromDisk() {
        Assert.fail("test not written yet");
        
        String assetPath = "fixtures/waverecipes/recipeone.waverecipe"
        
        // InputStream is = getInstrumentation().getContext().getAssets().open(assetPath);
    }
    
    // public void testGetUID() {
    //     String uid = 
    // }
}