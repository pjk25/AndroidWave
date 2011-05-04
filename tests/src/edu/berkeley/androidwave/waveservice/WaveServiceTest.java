// 
//  WaveServiceTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;


import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Basic test of the WaveService, that it can be started.  Further testing
 * is performed in
 * @see WaveServicePrivateInterfaceTest
 * @see WaveServicePublicInterfaceTest
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.WaveServiceTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveServiceTest extends ServiceTestCase<WaveService> {
    
    public WaveServiceTest() {
        super(WaveService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
     * Test basic startup/shutdown of Service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), WaveService.class);
        startService(startIntent); 
    }

    @SmallTest
    public void testPrivateBindable() {
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.setClass(getContext(), WaveService.class);
        IBinder service = bindService(startIntent);
        assertNotNull("service should not be null", service);
    }
    
    @SmallTest
    public void testPublicBindable() {
        Intent startIntent = new Intent(WaveService.ACTION_WAVE_SERVICE);
        IBinder service = bindService(startIntent);
        assertNotNull("service should not be null", service);
    }
}
