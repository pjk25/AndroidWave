// 
//  WaveRecipeTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.InvalidSignatureException;

import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;
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
    
    WaveRecipe recipeOne;
    
    protected void setUp()
        throws InvalidSignatureException {
            
        // build an instance from the fixture for other tests
        String fixturePath = "fixtures/waverecipes/recipeone.waverecipe";
        recipeOne = WaveRecipe.createFromDisk(fixturePath);
    }
    
    /**
     * testRetrieveRecipe
     *
     * Retrieve a recipe from a recipe authority.  Note that we need a
     * running recipe server for this.
     */
    public void testCreateFromID() {
        fail("test not written yet");
        
        String recipeID = "";
        int version = 0;
        
        WaveRecipe testRecipe = WaveRecipe.createFromID(recipeID, version);
        
        assertNotNull(testRecipe);
    }
    
    public void testPreconditions() {
        // test the values in the recipeOne fixture
        assertEqual("getID should match that of recipe xml", recipeOne.getID(), "edu.berkeley.waverecipe.AccelerometerMagnitude");
        assertEqual("getVersion should be the timestamp of the recipe's signature", recipeOne.getVersion(), "some version that I don't know yet");
        
        assertEqual("check name", recipeOne.getName(), "Accelerometer Magnitude");
        assertEqual("check description", recipeOne.getDescription(), "Measures intensity of motion of your device.  Representative of your activity level.");
        
        fail("remaining recipeOne fixture tests not written");
    }
    
}