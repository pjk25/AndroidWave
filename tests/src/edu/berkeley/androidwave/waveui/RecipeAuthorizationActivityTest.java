// 
//  RecipeAuthorizationActivityTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import java.io.File;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveui.RecipeAuthorizationActivityTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class RecipeAuthorizationActivityTest extends ActivityInstrumentationTestCase2<RecipeAuthorizationActivity> {
    
    File cachedRecipe = null;

    public RecipeAuthorizationActivityTest() {
        super("edu.berkeley.androidwave.waveui", RecipeAuthorizationActivity.class);
    }
    
    public void setUp() {
        Intent i = new Intent(WaveService.ACTION_AUTHORIZE);
        i.putExtra(WaveService.RECIPE_ID_EXTRA, "edu.berkeley.waverecipe.AccelerometerMagnitude");
        
        setActivityIntent(i);   // NOTE: Activities under test may not be started from within the UI thread. If your test method is annotated with UiThreadTest, then you must call setActivityIntent(Intent) from setUp().
    }

    @Override
    protected void tearDown() throws Exception {
        if (cachedRecipe != null && cachedRecipe.exists()) {
            if (cachedRecipe.delete()) {
                System.out.println("Removed "+cachedRecipe);
            } else {
                throw new Exception("Removal of "+cachedRecipe+" failed.");
            }
        }

        super.tearDown();
    }

    /**
     * Verifies that activity under test can be launched.
     */
    public void testActivityTestCaseSetUpProperly() {
        Activity a = getActivity();
        assertNotNull("activity should be launched successfully", a);
    }
    
    /**
     * Some UI testing
     */
    public void testUILayout() throws Exception {
        cachedRecipe = TestUtils.copyTestAssetToInternal(getInstrumentation().getTargetContext(), "fixtures/waverecipes/one.waverecipe", WaveRecipe.WAVERECIPE_CACHE_DIR+"/edu.berkeley.waverecipe.AccelerometerMagnitude.waverecipe");
        System.out.println("cachedRecipe => "+cachedRecipe);
        assertTrue("recipe is cached", cachedRecipe.exists());
        
        Activity a = getActivity();
        
        assertEquals("Accelerometer Magnitude", a.recipeName.getText());
    }
}
