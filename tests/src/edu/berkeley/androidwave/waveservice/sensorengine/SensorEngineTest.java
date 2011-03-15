// 
//  SensorEngineTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.*;

import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.util.Set;

/**
 * SensorEngineTest
 * 
 * @see SensorEngine
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.SensorEngineTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class SensorEngineTest extends InstrumentationTestCase {
    
    SensorEngine sensorEngineInstance;
    
    public void setUp() throws Exception {
        SensorEngine.init(getInstrumentation().getContext());
        sensorEngineInstance = SensorEngine.getInstance();
    }
    
    /**
     * testAvailableSensorsMatchingWaveSensorDescription
     * 
     * @see SensorEngine#availableSensorsMatchingWaveSensorDescription
     */
    @MediumTest
    public void testAvailableSensorsMatchingWaveSensorDescription() throws Exception {
        // we will test a sensorDescription that does not specify channels,
        // like the AccelerometerMagnitudeRecipe, to test imprecise matching
        WaveSensorDescription sensorDescription = new WaveSensorDescription(WaveSensor.Type.ACCELEROMETER, "-m/s^2");
        
        Set<WaveSensor> matchingSensorSet = sensorEngineInstance.availableSensorsMatchingWaveSensorDescription(sensorDescription);
        
        assertEquals("there should be 1 matching sensor", 1, matchingSensorSet.size());
        WaveSensor theMatchingSensor = matchingSensorSet.iterator().next();
        
        assertEquals("and it should be an accelerometer", WaveSensor.Type.ACCELEROMETER, theMatchingSensor.getType());
    }
    
    /**
     * testStartAndStopAndroidWaveSensor
     * 
     * @see SensorEngine#startAndroidWaveSensor
     * @see SensorEngine#stopAndroidWaveSensor
     */
    @MediumTest
    public void testStartAndStopAndroidWaveSensor() throws Exception {
        
        Set<WaveSensor> accelSensors = WaveSensor.getAvailableLocalSensor(getInstrumentation().getContext(), WaveSensor.Type.ACCELEROMETER);
        assertTrue(accelSensors.size() > 0);
        
        AndroidWaveSensor accelSensor = (AndroidWaveSensor)accelSensors.iterator().next();
        
        // start an accelerometer at 5.0Hz minimum
        sensorEngineInstance.startAndroidWaveSensor(accelSensor, 5.0);
        assertTrue(sensorEngineInstance.runningSensors.containsKey(accelSensor));
        assertEquals(sensorEngineInstance.runningSensors.get(accelSensor).doubleValue(), 5.0);
        
        // stop
        assertTrue(sensorEngineInstance.stopAndroidWaveSensor(accelSensor));
        assertFalse(sensorEngineInstance.runningSensors.containsKey(accelSensor));
        
        // re-stop fails
        assertFalse(sensorEngineInstance.stopAndroidWaveSensor(accelSensor));
    }
    
    /**
     * testSupportInfoForRecipe
     * 
     * @see SensorEngine#supportInfoForRecipe
     */
    @LargeTest
    public void testSupportInfoForRecipe() throws Exception {
        
        File targetFile = TestUtils.copyAssetToInternal(getInstrumentation(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipe = WaveRecipe.createFromDisk(getInstrumentation().getTargetContext(), targetFile.getPath());
        
        /**
         * we would like SensorEngine.waveRecipeCanBeSatisfied to report
         * the maximum precision and rate available for the inputs it
         * describes.  One should then be able to generate an authorization
         * object based on that recipe and those values.
         */
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngineInstance.supportInfoForRecipe(recipe);
        fail("test not finished yet");
    }
    
    /**
     * testScheduleWaveRecipeAuthorization
     * 
     * This is the big one, where the sensor engine fulfils sensing needs for
     * a recipe authorization
     */
    @LargeTest
    public void testScheduleWaveRecipeAuthorization() throws Exception {
        
        File targetFile = TestUtils.copyAssetToInternal(getInstrumentation(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipe = WaveRecipe.createFromDisk(getInstrumentation().getTargetContext(), targetFile.getPath());
        
        WaveRecipeAuthorization auth = new WaveRecipeAuthorization(recipe);
    }
}