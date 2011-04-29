// 
//  WaveServicePublicInterfaceTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-04-20.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

// import edu.berkeley.androidwave.PrivateAccessor;
import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;
import edu.berkeley.androidwave.waveclient.IWaveServicePublic;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveclient.WaveRecipeOutputDataImpl;

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
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.WaveServicePublicInterfaceTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveServicePublicInterfaceTest extends ServiceTestCase<WaveService> {
    
    IBinder service;
    IWaveServicePublic mService;
    
    File cachedRecipe;
    String recipeId;
    String clientKey;
    
    
    public WaveServicePublicInterfaceTest() {
        super(WaveService.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        service = bindService(startIntent);
        mService = IWaveServicePublic.Stub.asInterface(service);
        
        cachedRecipe = null;
        recipeId = "edu.berkeley.waverecipe.AccelerometerMagnitude";
        clientKey = "my_client_key";
        
        // TODO: clear the authorzation database
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
        assertNotNull(mService);
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
         * test the remote calls (without validating result)
         */
        mService.isAuthorized(clientKey, recipeId);
        mService.getAuthorizationIntent(recipeId, clientKey);
        mService.registerRecipeOutputListener(clientKey, recipeId, mListener);
        mService.unregisterRecipeOutputListener(clientKey, recipeId, mListener);
    }
    
    /**
     * test the isAuthorized call
     */
    @LargeTest
    public void testIsAuthorized() throws Exception {
        String recipeId = "edu.berkeley.waverecipe.AccelerometerMagnitude";
        // null key should always be unauthorized
        assertFalse(mService.isAuthorized(null, recipeId));
        
        // new key should be unauthorized
        assertFalse(mService.isAuthorized(clientKey, recipeId));
        
        // fake an authorization by accessing the private api of the service
        
        
        
        // need to simulate auth
        fail("test not finished");
    }
    
    /**
     * test the retrieveAuthorization call
     */
    @LargeTest
    public void testRetrieveAuthorization() throws Exception {
        WaveRecipeAuthorizationInfo auth;
        auth = mService.retrieveAuthorizationInfo(null, recipeId);
        assertNull("null key should return null auth", auth);
        
        auth = mService.retrieveAuthorizationInfo(clientKey, recipeId);
        assertNull("Test is not authorized and should return null", auth);
        
        // need to simulate authorization and re-call
        fail("test not finished");
    }
    
    /**
     * test the getAuthorizationIntent call
     */
    @LargeTest
    public void testGetAuthorizationIntent() throws Exception {
        Intent authIntent = mService.getAuthorizationIntent(recipeId, clientKey);
        
        assertNotNull(authIntent);
        assertEquals(WaveService.ACTION_REQUEST_RECIPE_AUTHORIZE, authIntent.getAction());
        assertEquals(recipeId, authIntent.getStringExtra(WaveService.RECIPE_ID_EXTRA));
        assertEquals(clientKey, authIntent.getStringExtra(WaveService.CLIENT_KEY_EXTRA));
    }
}