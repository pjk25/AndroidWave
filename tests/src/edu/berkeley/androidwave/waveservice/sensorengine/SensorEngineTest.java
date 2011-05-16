// 
//  SensorEngineTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.*;

import android.hardware.Sensor;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * SensorEngineTest
 * 
 * @see SensorEngine
 * 
 * This has become sort of an integration test, as it tests interaction
 * between the SensorEngine and WaveRecipe
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.SensorEngineTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class SensorEngineTest extends AndroidTestCase {
    
    SensorEngine sensorEngineInstance;
    
    public void setUp() throws Exception {
        SensorEngine.init(getContext());
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
        WaveSensorDescription sensorDescription = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        
        Set<WaveSensor> matchingSensorSet = sensorEngineInstance.availableSensorsMatchingWaveSensorDescription(sensorDescription);
        
        assertEquals("there should be 1 matching sensor", 1, matchingSensorSet.size());
        WaveSensor theMatchingSensor = matchingSensorSet.iterator().next();
        
        assertEquals("and it should be an accelerometer", WaveSensorDescription.Type.ACCELEROMETER, theMatchingSensor.getType());
    }
    
    /**
     * testStartAndStopAndroidWaveSensor
     * 
     * @see SensorEngine#startAndroidWaveSensor
     * @see SensorEngine#stopAndroidWaveSensor
     */
    @MediumTest
    public void testStartAndStopSensor() throws Exception {
        
        Set<WaveSensor> accelSensors = WaveSensor.getAvailableLocalSensors(getContext(), WaveSensorDescription.Type.ACCELEROMETER);
        assertTrue(accelSensors.size() > 0);
        
        AndroidWaveSensor accelSensor = (AndroidWaveSensor)accelSensors.iterator().next();
        Sensor s = accelSensor.getAndroidSensor();
        
        // start an accelerometer at 5.0Hz minimum without exception
        sensorEngineInstance.startSensor(s, 5.0);
        // re-start throws Exception
        try {
            sensorEngineInstance.startSensor(s, 10.0);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        // stop
        sensorEngineInstance.stopSensor(s);
        // re-stop throws Exception
        try {
            sensorEngineInstance.stopSensor(s);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
    
    /**
     * testSupportInfoForRecipe
     * 
     * also tests isSupported in WaveRecipeLocalDeviceSupportInfo, as this
     * requires use of a recipe and is a sort of integration test
     * 
     * @see SensorEngine#supportInfoForRecipe
     * @see WaveRecipeLocalDeviceSupportInfo#isSupported
     */
    @LargeTest
    public void testSupportInfoForRecipe() throws Exception {
        
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipe = WaveRecipe.createFromDisk(getContext(), targetFile);
        assertNotNull(recipe);
        
        /**
         * we would like SensorEngine.waveRecipeCanBeSatisfied to report
         * the maximum precision and rate available for the inputs it
         * describes.  One should then be able to generate an authorization
         * object based on that recipe and those values.
         */
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngineInstance.supportInfoForRecipe(recipe);
        assertNotNull(supportInfo);
        
        // fixtures are such that the recipe can be supported
        assertTrue(supportInfo.isSupported());
        
        // and further validate that
        Map<WaveSensorDescription, WaveSensor> descriptionToSensorMap = supportInfo.getDescriptionToSensorMap();
        
        for (WaveSensorDescription wsd : recipe.getSensors()) {
            assertTrue(descriptionToSensorMap.containsKey(wsd));
        }
    }
    
    /**
     * testScheduleWaveRecipeAuthorization
     * 
     * This is the big one, where the sensor engine fulfils sensing needs for
     * a recipe authorization
     */
    @LargeTest
    public void testScheduleWaveRecipeAuthorization() throws Exception {
        
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        WaveRecipe recipe = WaveRecipe.createFromDisk(getContext(), targetFile);
        
        //WaveRecipeAuthorization auth = new WaveRecipeAuthorization(recipe);
        
        
        
        fail("test not finished yet");
    }
}