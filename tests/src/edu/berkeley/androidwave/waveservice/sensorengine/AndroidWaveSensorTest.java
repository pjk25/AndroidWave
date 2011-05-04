// 
//  AndroidWaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-09.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.hardware.Sensor;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * AndroidWaveSensorTest
 * 
 * @see AndroidWaveSensor
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.AndroidWaveSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class AndroidWaveSensorTest extends AndroidTestCase {
    
    AndroidWaveSensor aws;
    
    @Override
    public void setUp() {
        aws = new AndroidWaveSensor(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
    }
    
    public void tesGetVersion() {
        String methodSig = TestUtils.methodSignature(aws,
                                                     "public",
                                                     false,
                                                     String.class,
                                                     "getVersion",
                                                     new Class[0]);
        TestUtils.assertHasMethod(methodSig, true, aws);
    }
    
    public void testGetAndroidSensor() {
        String methodSig = TestUtils.methodSignature(aws,
                                                     "public",
                                                     false,
                                                     Sensor.class,
                                                     "getAndroidSensor",
                                                     new Class[0]);
        TestUtils.assertHasMethod(methodSig, true, aws);
    }
}