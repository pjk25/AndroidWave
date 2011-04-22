// 
//  WaveServicePrivateInterfaceTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-04-20.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.PrivateAccessor;
import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveexception.WaveRecipeNotCachedException;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;

/**
 * This is a simple framework for a test of a Service.  See {@link android.test.ServiceTestCase
 * ServiceTestCase} for more information on how to write and extend service tests.
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.WaveServicePrivateInterfaceTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveServicePrivateInterfaceTest extends ServiceTestCase<WaveService> {
    
    File cachedRecipe = null;
    
    IBinder service;
    
    public WaveServicePrivateInterfaceTest() {
        super(WaveService.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.setClass(getContext(), WaveService.class);
        service = bindService(startIntent);
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
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as junit uses reflection to find the tests.
     */
    @SmallTest
    public void testPreconditions() {
        assertNotNull("service should not be null", service);
    }
    
    /**
     * test the recipeInCache method
     * 
     * This test evidences the caching structure
     */
    @SmallTest
    public void testRecipeInCacheFileForId() throws Exception {
        WaveService s = getService();
        assertNotNull(s);
        
        cachedRecipe = TestUtils.copyTestAssetToInternal(getSystemContext(), "fixtures/waverecipes/one.waverecipe", WaveService.WAVERECIPE_CACHE_DIR+"/edu.berkeley.waverecipe.AccelerometerMagnitude.waverecipe");
        System.out.println("cachedRecipe => "+cachedRecipe);
        
        File inCache = s.recipeCacheFileForId("edu.berkeley.waverecipe.AccelerometerMagnitude");
        assertTrue(inCache.exists());
        
        // now remove it
        if (!cachedRecipe.delete()) {
            throw new Exception("could not remove "+cachedRecipe);
        }

        try {
            inCache = s.recipeCacheFileForId("edu.berkeley.waverecipe.AccelerometerMagnitude");
        } catch (Exception e) {
            assertTrue(e instanceof WaveRecipeNotCachedException);
        }
    }
    
    /**
     * testPermitClientNameKeyPair
     */
    @MediumTest
    public void testPermitClientNameKeyPair() {
        WaveService s = getService();
        assertNotNull(s);
        
        String packageNameOne = "edu.berkeley.waveclientsample";
        String clientKeyOne = "some_random_key";
        
        String packageNameTwo = "com.android.somecoolapp";
        String clientKeyTwo = "some_other_key";
        
        // note that the auth db should be empty
        assertTrue(s.permitClientNameKeyPair(packageNameOne, clientKeyOne));
        assertTrue(s.permitClientNameKeyPair(packageNameOne, clientKeyOne));
        
        assertFalse(s.permitClientNameKeyPair(packageNameOne, clientKeyTwo));
        // here package two will attempt to use package one's key. This means
        // that we will revoke the key completely for protection
        assertFalse(s.permitClientNameKeyPair(packageNameTwo, clientKeyOne));
        // and now packageone is blocked
        assertFalse(s.permitClientNameKeyPair(packageNameOne, clientKeyOne));
        
        // now package two uses it's own key
        assertTrue(s.permitClientNameKeyPair(packageNameTwo, clientKeyTwo));
        assertTrue(s.permitClientNameKeyPair(packageNameTwo, clientKeyTwo));
    }
}