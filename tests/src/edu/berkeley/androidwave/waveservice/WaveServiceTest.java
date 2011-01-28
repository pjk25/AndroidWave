// 
//  WaveServiceTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * This is a simple framework for a test of a Service.  See {@link android.test.ServiceTestCase
 * ServiceTestCase} for more information on how to write and extend service tests.
 * 
 * To run this test, you can type:
 * adb shell am instrument -w \
 *   -e class edu.berkeley.androidwave.waveservice.WaveServiceTest \
 *   edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
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
    
    /**
     * Test binding to service
     *
     * This service should allow binding over two interfaces, one for the
     * Wave UI, and one for Wave Client Apps.
     */
    @MediumTest
    public void testPrivateBindable() {
        Intent startIntent = new Intent(Intent.ACTION_EDIT);
        startIntent.setClass(getContext(), WaveService.class);
        IBinder service = bindService(startIntent); 
    }
    
    @MediumTest
    public void testPublicBindable() {
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.setClass(getContext(), WaveService.class);
        IBinder service = bindService(startIntent); 
    }
}