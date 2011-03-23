// 
//  RecipeAuthorizationActivityTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

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
        super("edu.berkeley.androidwave.waveui", RecipeAuthorizationActivity.class);
    }
    
    public void setUp() {
        Intent i = new Intent(WaveService.ACTION_AUTHORIZE);
        i.putExtra(WaveService.RECIPE_ID_EXTRA, "edu.berkeley.waverecipe.AccelerometerMagnitude");
        
        setActivityIntent(i);   // NOTE: Activities under test may not be started from within the UI thread. If your test method is annotated with UiThreadTest, then you must call setActivityIntent(Intent) from setUp().
    }

    /**
     * Verifies that activity under test can be launched.
     */
    public void testActivityTestCaseSetUpProperly() {
        Activity a = getActivity();
        assertNotNull("activity should be launched successfully", a);
        assertEquals(RecipeAuthorizationActivity.class, a.getClass());
    }
}
