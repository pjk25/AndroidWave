// 
//  WaveServiceTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.PrivateAccessor;
import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;
import edu.berkeley.androidwave.waveclient.IWaveServicePublic;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveclient.WaveRecipeOutputDataImpl;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;

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
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.WaveServiceTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveServiceTest extends ServiceTestCase<WaveService> {
    
    File cachedRecipe = null;
    
    public WaveServiceTest() {
        super(WaveService.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
    }
    
    /**
     * test the recipeInCache private method
     * 
     * This test evidences the caching structure
     */
    @SmallTest
    public void recipeInCache() throws Exception {
        WaveService s = getService();
        
        cachedRecipe = TestUtils.copyTestAssetToInternal(getSystemContext(), "fixtures/waverecipes/one.waverecipe", WaveRecipe.WAVERECIPE_CACHE_DIR+"/edu.berkeley.waverecipe.AccelerometerMagnitude.waverecipe");
        System.out.println("cachedRecipe => "+cachedRecipe);
        
        // use PrivateAccessor to call the private method
        boolean inCache = (Boolean) PrivateAccessor.invokePrivateMethod(s, "recipeInCache", "edu.berkeley.waverecipe.AccelerometerMagnitude");
        assertTrue(inCache);
        
        // now remove it
        if (!cachedRecipe.delete()) {
            throw new Exception("could not remove "+cachedRecipe);
        }
        
        inCache = (Boolean) PrivateAccessor.invokePrivateMethod(s, "recipeInCache", "edu.berkeley.waverecipe.AccelerometerMagnitude");
        assertFalse(inCache);
    }
    
    /**
     * Test basic startup/shutdown of Service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), WaveService.class);
        startService(startIntent); 
    }
    
    /**
     * Test binding to service
     *
     * This service should allow binding over two interfaces, one for the
     * Wave UI, and one for Wave Client Apps.
     */
    @MediumTest
    public void testPrivateBindable() {
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.setClass(getContext(), WaveService.class);
        IBinder service = bindService(startIntent);
        assertNotNull("service should not be null", service);
    }
    
    @MediumTest
    public void testPublicBindable() {
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent);
        assertNotNull("service should not be null", service);
    }
    
    /**
     * test public interface recipe interaction
     */
    @MediumTest
    public void testPublicBindableMore() throws RemoteException {
        
        /**
         * Simple recipeListener
         */
        IWaveRecipeOutputDataListener mListener = new IWaveRecipeOutputDataListener.Stub() {
            public void receiveWaveRecipeOutputData(WaveRecipeOutputDataImpl wrOutput) {
                System.out.println("WaveRecipeOutputDataImpl received => "+wrOutput);
            }
        };
        
        /**
         * test the remote calls
         */
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent);
        
        IWaveServicePublic mService = IWaveServicePublic.Stub.asInterface(service);
        assertNotNull(mService);
        
        // for now just make the remote call, without validating the result
        mService.isAuthorized("edu.berkeley.waverecipe.AccelerometerMagnitude");
        mService.getAuthorizationIntent("edu.berkeley.waverecipe.AccelerometerMagnitude");
        mService.registerRecipeOutputListener(mListener, false);
        mService.unregisterRecipeOutputListener(mListener);
    }
    
    /**
     * test the isAuthorized call
     */
    @LargeTest
    public void testIsAuthorized() throws Exception {
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent); 
        
        IWaveServicePublic mService = IWaveServicePublic.Stub.asInterface(service);
        
        assertFalse(mService.isAuthorized("edu.berkeley.waverecipe.AccelerometerMagnitude"));
        
        // need to simulate auth
        fail("test not finished");
    }
    
    /**
     * test the retrieveAuthorization call
     */
    @LargeTest
    public void testRetrieveAuthorization() throws Exception {
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent); 
        
        IWaveServicePublic mService = IWaveServicePublic.Stub.asInterface(service);
        
        WaveRecipeAuthorizationInfo auth = mService.retrieveAuthorizationInfo("edu.berkeley.waverecipe.AccelerometerMagnitude");
        assertNull("Test is not authorized and should return null", auth);
        
        // need to simulate authorization and re-call
        fail("test not finished");
    }
    
    /**
     * test the getAuthorizationIntent call
     */
    @LargeTest
    public void testGetAuthorizationIntent() throws Exception {
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent); 
        
        IWaveServicePublic mService = IWaveServicePublic.Stub.asInterface(service);
        
        Intent authIntent = mService.getAuthorizationIntent("edu.berkeley.waverecipe.AccelerometerMagnitude");
        
        assertNotNull(authIntent);
        assertEquals(WaveService.ACTION_REQUEST_RECIPE_AUTHORIZE, authIntent.getAction());
        assertEquals("edu.berkeley.waverecipe.AccelerometerMagnitude", authIntent.getStringExtra(WaveService.RECIPE_ID_EXTRA));
    }
}
