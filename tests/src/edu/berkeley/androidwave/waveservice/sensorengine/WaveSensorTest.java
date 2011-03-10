// 
//  WaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.os.Build;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.Set;

/**
 * WaveSensor/WaveSensorChannel test
 * 
 * @see WaveSensor, @see WaveSensorChannel
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveSensorTest extends AndroidTestCase {
    
    protected WaveSensor fakeAccelerometer;
    
    @Override
    protected void setUp() {
        // create fixture sensors
        fakeAccelerometer = new WaveSensor(WaveSensor.Type.ACCELEROMETER);
        
        WaveSensorChannel[] channels = { new WaveSensorChannel("X"),
                                         new WaveSensorChannel("Y"),
                                         new WaveSensorChannel("Z")};
        
        fakeAccelerometer.channels = channels;
    }
    
    @SmallTest
    public void testGetType() throws Exception {
        assertEquals("fakeAccelerometer has type ACCELEROMETER", WaveSensor.Type.ACCELEROMETER, fakeAccelerometer.getType());
    }
    
    @SmallTest
    public void testGetVersion() throws Exception {
        
        String device = Build.DEVICE;
        String board = Build.BOARD;
        String model = Build.MODEL;
        
        String expectedVersion = device + "_" + board + "_" + model;
        
        assertEquals(expectedVersion, fakeAccelerometer.getVersion());
    }
    
    /**
     * testHasChannels
     * 
     * By definition, a WaveSensor has at least one channel
     */
    @MediumTest
    public void testHasChannels() throws Exception {
        assertNotNull(fakeAccelerometer.getChannels());
        assertTrue(fakeAccelerometer.getChannels().length >= 1);
    }
    
    @MediumTest
    public void testGetChannels() throws Exception {
        WaveSensorChannel[] channels = fakeAccelerometer.getChannels();
        assertEquals("fakeAccelerometer has 3 channels", 3, channels.length);
        // channels themselves are tested in WaveSensorChannelTest
    }
    
    /**
     * Static Method Tests
     */
    
    /**
     * testGetAvailableLocalSensors
     * 
     * getAvailableLocalSensors should return a list of WaveSensor instances
     * representing the sensors physically present on the device.  This
     * typically includes, at a minumum, accelerometer, magnetometer, and GPS
     */
    @LargeTest
    public void testGetAvailableLocalSensors() throws Exception {
        
        Set<WaveSensor> localSensors = WaveSensor.getAvailableLocalSensors(getContext());
        
        assertNotNull("getAvailableLocalSensors() should not return null", localSensors);
        
        //assertEquals("Should have 3 available sensors", 3, localSensors.size());
        
        // there should be accelerometer, magnetometer, and location
        WaveSensor accelSensor = null;
        WaveSensor magSensor = null;
        WaveSensor locSensor = null;
        for (WaveSensor s : localSensors) {
            if (s.getType() == WaveSensor.Type.ACCELEROMETER) {
                accelSensor = s;
            } else if (s.getType() == WaveSensor.Type.MAGNETOMETER) {
                magSensor = s;
            } else if (s.getType() == WaveSensor.Type.LOCATION) {
                locSensor = s;
            }
        }
        
        // accelerometer
        assertNotNull("should have accelerometer", accelSensor);
        WaveSensorChannel[] accelChannels = accelSensor.getChannels();
        assertNotNull(accelChannels);
        assertEquals("Accelerometer has 3 channels", 3, accelChannels.length);
        
        // magnetometer
        assertNull("emulated device has no magnetometer", magSensor);
        // WaveSensorChannel[] magChannels = magSensor.getChannels();
        // assertNotNull(magChannels);
        // assertEquals("Magnetometer has 3 channels", 3, magChannels.length);
        
        // location
        assertNotNull("should have location", locSensor);
    }
} 