// 
//  WaveSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;

import android.os.Build;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.Iterator;
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
        fakeAccelerometer = new WaveSensor(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        
        WaveSensorChannel[] channels = { new WaveSensorChannel("X"),
                                         new WaveSensorChannel("Y"),
                                         new WaveSensorChannel("Z")};
        
        fakeAccelerometer.channels = channels;
    }
    
    /**
     * by definition all WaveSensor instances have a type and associated unit.
     * Thus, it should not be possible to instantiate a WaveSensor without
     * them.
     */
    @SmallTest
    public void testNullParamsInConstructorThrowsNullPointerException() {
        try {
            new WaveSensor(WaveSensorDescription.Type.MAGNETOMETER, null);
        } catch (Exception ex) {
            System.out.println("testNullParamsInConstructorThrowsNullPointerException:\tex => "+ex);
            assertEquals(NullPointerException.class, ex.getClass());
        }
        
        try {
            new WaveSensor(null, "-m/s^2");
        } catch (Exception ex) {
            assertEquals(NullPointerException.class, ex.getClass());
        }
    }
    
    @SmallTest
    public void testGetType() throws Exception {
        assertEquals("fakeAccelerometer has type ACCELEROMETER", WaveSensorDescription.Type.ACCELEROMETER, fakeAccelerometer.getType());
    }
    
    @SmallTest
    public void testGetVersion() throws Exception {
        
        String device = Build.DEVICE;
        String board = Build.BOARD;
        String model = Build.MODEL;
        
        String expectedVersion = device + "_" + board + "_" + model + "_ACCELEROMETER";
        
        assertEquals(expectedVersion, fakeAccelerometer.getVersion());
    }
    
    /**
     * testGetUnits
     * 
     * all WaveSensors should specify the unit of their measure (which is by
     * definition common to all of its channels).
     */
    @SmallTest
    public void testGetUnits() {
        TestUtils.assertHasMethod("public java.lang.String edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor.getUnits()", fakeAccelerometer);
        
        // specific to the fakeAccelerometer fixture
        assertEquals("-m/s^2", fakeAccelerometer.getUnits());
    }
    
    /**
     * testGetHighestAvailablePrecision
     * 
     * highest precision and highest sampling frequency are currently hard
     * coded estimates
     */
    @SmallTest
    public void testGetMaximumAvailablePrecision() throws Exception {
        /**
         * generic_unknown_sdk corresponds to the emulator.  It is asserted
         * here to ensure that this test fails on an actual device, until we
         * known accurate precision values for other devices
         */ 
        assertEquals("accel version", "generic_unknown_sdk_ACCELEROMETER", fakeAccelerometer.getVersion());
        assertEquals("max accelerometer precision", (9.81/1024.0), fakeAccelerometer.getMaximumAvailablePrecision());
    }
    
    /**
     * testGetHighestAvailableSamplingFrequency
     * 
     * highest precision and highest sampling frequency are currently hard
     * coded estimates
     */
    @SmallTest
    public void testGetMaximumAvailableSamplingFrequency() throws Exception {
        /**
         * generic_unknown_sdk corresponds to the emulator.  It is asserted
         * here to ensure that this test fails on an actual device, until we
         * known accurate precision values for other devices
         */ 
        assertEquals("accel version", "generic_unknown_sdk_ACCELEROMETER", fakeAccelerometer.getVersion());
        assertEquals("max accel sampling frequency", 10.0, fakeAccelerometer.getMaximumAvailableSamplingFrequency());
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
    
    /**
     * testGetChannels
     * 
     * Channels accessor test
     */
    @MediumTest
    public void testGetChannels() throws Exception {
        WaveSensorChannel[] channels = fakeAccelerometer.getChannels();
        assertEquals("fakeAccelerometer has 3 channels", 3, channels.length);
        // channels themselves are tested in WaveSensorChannelTest
    }
    
    /**
     * testMatchesWaveSensorDescription
     */
    @MediumTest
    public void testMatchesWaveSensorDescription() {
        Class[] argClasses = { WaveSensorDescription.class };
        String sig = TestUtils.methodSignature(WaveSensor.class,
                                               "public",
                                               false,
                                               boolean.class,
                                               "matchesWaveSensorDescription",
                                               argClasses);
        TestUtils.assertHasMethod(sig, true, fakeAccelerometer);
        
        // contsruct a matching WaveSensorDescription
        WaveSensorDescription matchingWsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        // check matches without channels
        assertTrue(fakeAccelerometer.matchesWaveSensorDescription(matchingWsd));
        // check matches with channels
        matchingWsd.addChannel(new WaveSensorChannelDescription("X"));
        matchingWsd.addChannel(new WaveSensorChannelDescription("Y"));
        matchingWsd.addChannel(new WaveSensorChannelDescription("Z"));
        assertTrue(fakeAccelerometer.matchesWaveSensorDescription(matchingWsd));
        
        // construct a non-matching WaveSensorDescription
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "g");
        assertFalse(fakeAccelerometer.matchesWaveSensorDescription(wsd));
        wsd = new WaveSensorDescription(WaveSensorDescription.Type.MAGNETOMETER, "uT");
        assertFalse(fakeAccelerometer.matchesWaveSensorDescription(wsd));
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
        
        assertEquals("Emulator should have 2 available sensors", 2, localSensors.size());
        
        // there should be accelerometer, magnetometer, and location
        WaveSensor accelSensor = null;
        WaveSensor magSensor = null;
        WaveSensor locSensor = null;
        for (WaveSensor s : localSensors) {
            if (s.getType() == WaveSensorDescription.Type.ACCELEROMETER) {
                accelSensor = s;
            } else if (s.getType() == WaveSensorDescription.Type.MAGNETOMETER) {
                magSensor = s;
            } else if (s.getType() == WaveSensorDescription.Type.LOCATION) {
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
    
    /**
     * testGetAvailableLocalSensor
     * 
     * similar to {@link WaveSensorTest testGetAvailableLocalSensors}, but
     * retreiving only one type of sensor
     */
    @LargeTest
    public void testGetAvailableLocalSensorsByType() throws Exception {
        Set<WaveSensor> localSensors = WaveSensor.getAvailableLocalSensors(getContext(), WaveSensorDescription.Type.ACCELEROMETER);
        
        assertNotNull("getAvailableLocalSensors() should not return null", localSensors);
        
        for (WaveSensor s : localSensors) {
            assertEquals(WaveSensorDescription.Type.ACCELEROMETER, s.getType());
        }
    }
    
    @LargeTest
    public void testToFromInternalId() {
        Set<WaveSensor> localSensors = WaveSensor.getAvailableLocalSensors(getContext());
        WaveSensor original = null;
        Iterator<WaveSensor> it = localSensors.iterator();
        if (it.hasNext()) {
            original = it.next();
            if (it.hasNext()) {
                original = it.next();
            }
        }
        assertNotNull(original);
        String id = original.internalId();
        assertNotNull(id);
        
        WaveSensor restored = WaveSensor.getSensorForInternalId(getContext(), "some_fake_id");
        assertNull(restored);
        
        restored = WaveSensor.getSensorForInternalId(getContext(), id);
        assertEquals("internalId for fakeAccelerometer restores fakeAccelerometer", original, restored);
    }
} 