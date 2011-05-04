// 
//  RecipeAuthorizationActivityTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
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
    
    public RecipeAuthorizationActivityTest() {
        // TODO: consider adjusting package name
        super("edu.berkeley.androidwave.waveui", RecipeAuthorizationActivity.class);
    }
    
    public void setUp() {
        Intent i = new Intent(WaveService.ACTION_REQUEST_RECIPE_AUTHORIZE);
        i.putExtra(WaveService.RECIPE_ID_EXTRA, "edu.berkeley.waverecipe.AccelerometerMagnitude");
        i.putExtra(WaveService.CLIENT_KEY_EXTRA, "sahtsthoesntaeonstdeasnthioasnh");
        
        setActivityIntent(i);   // NOTE: Activities under test may not be started from within the UI thread. If your test method is annotated with UiThreadTest, then you must call setActivityIntent(Intent) from setUp().
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
        File cachedRecipe = TestUtils.copyTestAssetToInternal(getInstrumentation().getTargetContext(), "fixtures/waverecipes/one.waverecipe", WaveService.WAVERECIPE_CACHE_DIR+"/edu.berkeley.waverecipe.AccelerometerMagnitude.waverecipe");
        System.out.println("cachedRecipe => "+cachedRecipe);
        assertTrue("recipe is cached", cachedRecipe.exists());
        
        RecipeAuthorizationActivity a = getActivity();
        
        // below won't be true until after binding has occurred
        // assertEquals("Accelerometer Magnitude", a.recipeName.getText());
        
        /*
        a.runOnUiThread(
            new Runnable() {
                public void run() {
                    assertEquals("Accelerometer Magnitude", a.recipeName.getText());
                } // end of run() method definition
            } // end of anonymous Runnable object instantiation
        ); // end of invocation of runOnUiThread
         */
    }
}
