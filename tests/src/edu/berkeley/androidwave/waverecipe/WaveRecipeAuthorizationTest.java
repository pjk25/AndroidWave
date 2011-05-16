// 
//  WaveRecipeAuthorizationTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-02-04.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;

import android.content.ComponentName;
import android.content.pm.Signature;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.util.Date;
import java.util.HashMap;

/**
 * WaveRecipeAuthorizationTest
 * 
 * Unit test for the WaveRecipeAuthorization class
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorizationTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeAuthorizationTest extends AndroidTestCase {
    
    WaveRecipe recipeOne;
    WaveRecipeAuthorization auth;
    
    protected void setUp() throws Exception {
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        recipeOne = WaveRecipe.createFromDisk(getContext(), targetFile);
        
        auth = new WaveRecipeAuthorization(recipeOne);
    }
    
    /**
     * ensure that a WaveRecipeAuthorization can be constructed with a
     * {@code WaveRecipe} argument
     */
    @SmallTest
    public void testConstructor() throws Exception {
        assertNotNull(auth);
    }
    
    @SmallTest
    public void testValidForDate() {
        Date now = new Date();
        Date before = new Date(now.getTime() - (10 * 1000)); // 10 seconds ago
        Date longBefore = new Date(now.getTime() - (100 * 60 * 60 * 1000)); // 100 hours ago
        Date soon = new Date(now.getTime() + (10 * 1000)); // 10 seconds from now
        Date notSoSoon = new Date(now.getTime() + (10 * 60 * 60 * 1000)); // 10 minutes from now
        
        assertTrue(before.before(now));
        assertTrue(longBefore.before(before));
        assertTrue(soon.after(now));
        assertTrue(notSoSoon.after(soon));
        
        // auth object from setup has no timestamps yet
        assertFalse(auth.validForDate(now));
        
        auth.setAuthorizedDate(before);
        assertTrue(auth.validForDate(now));
        assertFalse(auth.validForDate(longBefore));
        
        auth.setRevokedDate(soon);
        assertTrue(auth.validForDate(now));
        assertFalse(auth.validForDate(notSoSoon));
        assertFalse(auth.validForDate(before));
    }
    
    /**
     * test the asInfo method, which produces a WaveRecipeAuthorizationInfo
     * object.
     */
    @SmallTest
    public void testAsInfo() {
        WaveRecipeAuthorizationInfo info = auth.asInfo();
        assertNotNull(info);
        
        assertEquals(recipeOne.getId(), info.recipeId);
        assertEquals(recipeOne.getOutput(), info.recipeOutputDescription);
        assertFalse("output max rate should not be negative", info.outputMaxRate > 0.0);
        assertFalse("output max precision should not be negative", info.outputMaxPrecision > 0.0);
    }
    
    /**
     * test to/from JSON String
     */
    @MediumTest
    public void testToFromJSONString() throws Exception {
        WaveRecipeAuthorization original = auth;
        // need to populate the recipeClient data in the recipe before
        // creating json
        auth.recipeClientName = new ComponentName("edu.berkeley.waveclientsample.WaveClientSample", ".SomeActivity");
        auth.recipeClientSignatures = new Signature[1];
        auth.recipeClientSignatures[0] = new Signature("");
        
        Date now = new Date();
        auth.authorizedDate = now;
        auth.modifiedDate = now;
        
        String jsonString = original.toJSONString();
        
        assertEquals("matched WaveRecipe object", recipeOne, original.recipe);
        
        WaveRecipeAuthorization restored = WaveRecipeAuthorization.fromJSONString(recipeOne, jsonString);
        
        assertEquals(original, restored);
    }
}