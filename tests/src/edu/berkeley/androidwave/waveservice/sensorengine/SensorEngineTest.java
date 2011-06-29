// 
//  SensorEngineTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.*;
import edu.berkeley.androidwave.waveservice.sensorengine.sensors.WaveSensor;

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
     * testGetAvailableLocalSensors
     * 
     * getAvailableLocalSensors should return a list of WaveSensor instances
     * representing the sensors physically present on the device.  This
     * typically includes, at a minumum, accelerometer, magnetometer, and GPS
     * 
     * Note: this test expects to be run on the simulator, which should report
     * a single accelerometer
     */
    @LargeTest
    public void testGetAvailableLocalSensors() throws Exception {
        
        Set<WaveSensor> localSensors = sensorEngineInstance.getAvailableLocalSensors();
        
        assertNotNull("getAvailableLocalSensors() should not return null", localSensors);
        
        assertEquals("Emulator should have 2 available sensors", 2, localSensors.size());
        
        // there should be accelerometer, magnetometer, and location
        WaveSensor accelSensor = null;
        WaveSensor magSensor = null;
        WaveSensor locSensor = null;
        for (WaveSensor s : localSensors) {
            if (s.getType().equals("ACCELEROMETER")) {
                accelSensor = s;
            } else if (s.getType().equals("MAGNETIC_FIELD")) {
                magSensor = s;
            } else if (s.getType().equals("LOCATION")) {
                locSensor = s;
            }
        }
        
        // accelerometer
        assertNotNull("should have accelerometer", accelSensor);
        assertEquals("-m/s^2", accelSensor.getUnits());
        assertEquals("Accelerometer has 3 channels", 3, accelSensor.getChannels().size());
        
        // magnetometer
        assertNull("emulated device has no magnetometer", magSensor);
        
        // location
        assertNotNull("should have location", locSensor);
    }

    /**
     * testToFromInternalIdForSensor
     * 
     * TODO: fix, pulled from WaveSensorTest
     */
    @SmallTest
    public void testToFromInternalIdForSensor() {
        // test null id throws exception
        try {
            sensorEngineInstance.sensorForInternalId(null);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
        
        // test bad id returns null
        assertNull(sensorEngineInstance.sensorForInternalId("some_obviously_invalid_id"));
        
        // test working case
        Set<WaveSensor> localSensors = sensorEngineInstance.getAvailableLocalSensors();
        for (WaveSensor original : localSensors) {
            String id = sensorEngineInstance.internalIdForSensor(original);
            assertNotNull("id for "+original, id);
            
            WaveSensor restored = sensorEngineInstance.sensorForInternalId(id);
            assertEquals(original, restored);
        }
    }
    
    /**
     * testSupportInfoForRecipe
     * 
     * also tests isSupported in WaveRecipeLocalDeviceSupportInfo, as this
     * requires use of a recipe and is a sort of integration test
     * 
     * Note: this test expects to be run on the simulator, which should report
     * a single accelerometer
     * 
     * @see SensorEngine#supportInfoForRecipe
     * @see WaveRecipeLocalDeviceSupportInfo#isSupported
     */
    @LargeTest
    public void testSupportInfoForRecipe() throws Exception {
        
        WaveRecipe recipe = WaveRecipeTest.getFixtureOne(getContext());
        assertNotNull("recipe should not be null", recipe);
        
        /**
         * we would like SensorEngine.waveRecipeCanBeSatisfied to report
         * the maximum precision and rate available for the inputs it
         * describes.  One should then be able to generate an authorization
         * object based on that recipe and those values.
         */
        WaveRecipeLocalDeviceSupportInfo supportInfo = sensorEngineInstance.supportInfoForRecipe(recipe);
        assertNotNull("supportInfo should not be null", supportInfo);
        
        // fixtures are such that the recipe can be supported
        assertTrue("isSupported() should be true", supportInfo.isSupported());
        
        // and further validate that
        Map<WaveSensorDescription, WaveSensor> descriptionToSensorMap = supportInfo.getDescriptionToSensorMap();
        
        for (WaveSensorDescription wsd : recipe.getSensors()) {
            assertTrue(descriptionToSensorMap.containsKey(wsd));
        }
    }
    
    /**
     * testScheduleWaveRecipeAuthorization
     * 
     * We do not currently test this in an automated way, but instead rely on
     * our sample client applications for testing
     */
}