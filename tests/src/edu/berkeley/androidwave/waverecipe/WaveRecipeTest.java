// 
//  WaveRecipeTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.InvalidSignatureException;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import android.content.Context;
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
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeTest extends InstrumentationTestCase {
    
    WaveRecipe recipeOne;
    
    private void copyAssetToInternal(String source, String dest)
        throws IOException {
        
        Context targetContext = getInstrumentation().getTargetContext();
        String[] destComponents = dest.split("/", 2);
        if (destComponents.length > 0) {
            targetContext.getDir(destComponents[0], Context.MODE_PRIVATE);
        }
        InputStream is = getInstrumentation().getContext().getAssets().open(source);
        OutputStream os = targetContext.openFileOutput(dest, Context.MODE_PRIVATE);
        
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        is.close();
        os.close();
    }
    
    protected void setUp()
        throws InvalidSignatureException, IOException {
        
        // build an instance from the fixture for other tests
        // first copy the fixture to the recipes cache
        String cachePath = "waverecipes/one.waverecipe";
        copyAssetToInternal("fixtures/waverecipes/one.waverecipe", cachePath);
        MoreAsserts.assertContentsInAnyOrder("fixture should have been copied to cache", Arrays.asList(getInstrumentation().getTargetContext().fileList()), cachePath);
        recipeOne = WaveRecipe.createFromDisk(cachePath);
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
    
    public void testPreconditions()
        throws ParseException {
        // test the values in the recipeOne fixture
        assertEquals("getID should match that of recipe xml", recipeOne.getID(), "edu.berkeley.waverecipe.AccelerometerMagnitude");
        
        Date versionDate = DateFormat.getDateInstance().parse("2011-01-09T19:20:30.45-08:00");
        assertEquals("getVersion should be the timestamp of the recipe's signature", recipeOne.getVersion(), versionDate);
        
        assertEquals("check name", recipeOne.getName(), "Accelerometer Magnitude");
        
        assertEquals("check description", recipeOne.getDescription(), "Measures intensity of motion of your device.  Representative of your activity level.");
        
        fail("remaining recipeOne fixture tests not written");
    }
    
}