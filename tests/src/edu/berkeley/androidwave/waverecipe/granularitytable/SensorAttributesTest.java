// 
//  SensorAttributesTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-03.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.test.MoreAsserts;
import junit.framework.TestCase;

/**
 * SensorAttributesTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.granularitytable.SensorAttributesTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class SensorAttributesTest extends TestCase {
    
    public void testPreconditions() {
        
        SensorAttributes one = getFixtureOne();
        SensorAttributes two = getFixtureTwo();
        
        SensorAttributes likeOne = new SensorAttributes();
        likeOne.sensorDescription = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "m/s^2");
        likeOne.rate = 10.0;
        likeOne.precision = 0.01;
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, one, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, likeOne, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
    }
    
    /**
     * FIXTURES
     */
    
    public static SensorAttributes getFixtureOne() {
        SensorAttributes sa = new SensorAttributes();
        sa.sensorDescription = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "m/s^2");
        sa.rate = 10.0;
        sa.precision = 0.01;
        return sa;
    }
    
    public static SensorAttributes getFixtureTwo() {
        SensorAttributes sa = new SensorAttributes();
        sa.sensorDescription = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "g");
        sa.rate = 20.0;
        sa.precision = 0.001;
        return sa;
    }
}