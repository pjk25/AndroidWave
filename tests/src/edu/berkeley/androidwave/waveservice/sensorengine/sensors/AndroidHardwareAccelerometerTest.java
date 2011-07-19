// 
//  AndroidHardwareAccelerometerTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import java.util.Map;
import java.util.Set;

/**
 * AndroidHardwareAccelerometerTest
 * 
 * @see AndroidHardwareSensor
 * @see WaveSensor
 * @see WaveSensorChannel
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.sensors.AndroidHardwareAccelerometerTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class AndroidHardwareAccelerometerTest extends AndroidTestCase {
    
    private static final String TAG = AndroidHardwareAccelerometerTest.class.getSimpleName();

    class HatWaveRecipeAlgorithm implements WaveRecipeAlgorithm {
        
        int eventCount;
        long lastTime;
        Map<String, Double> lastValues;
        
        HatWaveRecipeAlgorithm() {
            eventCount = 0;
            lastTime = 0;
            lastValues = null;
        }

        public boolean setWaveRecipeAlgorithmListener(Object listener) {
            // return true so data will start to flow
            return true;
        }

        public void ingestSensorData(long time, Map<String, Double>values) {
            eventCount++;
            lastTime = time;
            lastValues = values;
            try {
                AndroidHardwareAccelerometerTest.this.notify();
            } catch (IllegalMonitorStateException imse) {}
        }
    }
    
    class SlimWaveRecipeAlgorithm implements WaveRecipeAlgorithm {
        
        int eventCount;
        
        SlimWaveRecipeAlgorithm() {
            eventCount = 0;
        }

        public boolean setWaveRecipeAlgorithmListener(Object listener) {
            // return true so data will start to flow
            return true;
        }

        public void ingestSensorData(long time, Map<String, Double>values) {
            eventCount++;
        }
    }

    /**
     * testInstancesAvailableInContext
     * 
     * Note: this test expects to be run on the simulator, which should report
     * a single accelerometer
     */
    @MediumTest
    public void testPreconditions() {
        Set<WaveSensor> sensors = AndroidHardwareAccelerometer.instancesAvailableInContext(getContext());
        assertNotNull(sensors);
        assertEquals(1, sensors.size());
        for (WaveSensor ws : sensors) {
            MoreAsserts.assertAssignableFrom(AndroidHardwareAccelerometer.class, ws);
        }
    }
    
    /**
     * Make sure getting our fixtures doesn't throw an Exception
     */
    @SmallTest
    public void testFixtures() {
        assertNotNull(getFixtureOne(getContext()));
    }
    
    @SmallTest
    public void testConstructor() {
        // test null units throws exception
        SensorManager sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        try {
            new AndroidHardwareAccelerometer(sensorManager, null);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
    
    @MediumTest
    public void testRegisterListener() throws Exception {
        
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());
        
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        wsd.addChannel(new WaveSensorChannelDescription("y"));
        wsd.addChannel(new WaveSensorChannelDescription("z"));
        
        HatWaveRecipeAlgorithm alg = new HatWaveRecipeAlgorithm();
        
        synchronized(this) {
            // successful start
            fixtureOne.registerListener(alg, wsd, 5.0, 0.1);
            
            // start after start throws exception
            try {
                fixtureOne.registerListener(alg, wsd, 5.0, 0.1);
            } catch (Exception e) {
                assertTrue(e instanceof Exception);
            }
            
            // data is received
            Log.d(TAG, "waiting for data...");
            this.wait(2*1000);    // wait up to 2 seconds
            Log.d(TAG, "checking for data");
            assertNotNull(alg.lastValues);
        }
        
        // test stop
        fixtureOne.unregisterListener(alg);
        synchronized(this) {
            int c = alg.eventCount;
            this.wait(1*1000);  // wait 1 second for more data
            assertEquals("eventCount should stay same", c, alg.eventCount);
        }
    }
    
    /**
     * testSensorDispatchRate
     * 
     * NOTE: This test fails on the droid, even though the
     *       AndroidWaveTesterClient
     *       (https://github.com/pjk25/AndroidWaveTesterClient) can receive
     *       accelerometer data at 30+ Hz.
     */
    @LargeTest
    public void testSensorDispatchRate() throws Exception {
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());
        
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        wsd.addChannel(new WaveSensorChannelDescription("y"));
        wsd.addChannel(new WaveSensorChannelDescription("z"));
        
        SlimWaveRecipeAlgorithm alg = new SlimWaveRecipeAlgorithm();
        
        double requestedRate = 5.0;
        int testDuration = 10*1000; // in milliseconds
        
        long startTime = SystemClock.elapsedRealtime();
        fixtureOne.registerListener(alg, wsd, requestedRate, 0.001);
        
        try {
            Thread.sleep(testDuration);
        } catch (InterruptedException ie) {}
        
        fixtureOne.unregisterListener(alg);
        long stopTime = SystemClock.elapsedRealtime();
        int receivedSamples = alg.eventCount;
        
        double realDuration = (stopTime - startTime) / 1000.0; // includes millisecond conversion
        int expectedSamples = (int) (requestedRate * realDuration);
        // rate is not exceeded
        assertTrue("requested rate not exceeded", receivedSamples <= expectedSamples);
        // 90% of expected samples were received
        String msg = String.format("75%% of requested rate met (%d received, %d expected)", receivedSamples, expectedSamples);
        assertTrue(msg, receivedSamples >= 0.75 * expectedSamples);
    }
    
    /**
     * testMatchesWaveSensorDescription
     * 
     * Note: this test expects to be run on the simulator, which should report
     * a single accelerometer
     */
    @MediumTest
    public void testMatchesWaveSensorDescription() {
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());

        WaveSensorDescription sensorDescriptionOne = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        WaveSensorDescription sensorDescriptionTwo = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "g");
        
        assertTrue(fixtureOne.matchesWaveSensorDescription(sensorDescriptionOne));
        assertFalse(fixtureOne.matchesWaveSensorDescription(sensorDescriptionTwo));
    }
    
    /**
     * FIXTURES
     */
    
    public static AndroidHardwareAccelerometer getFixtureOne(Context c) {
        Set<WaveSensor> sensors = AndroidHardwareAccelerometer.instancesAvailableInContext(c);
        for (WaveSensor ws : sensors) {
            return (AndroidHardwareAccelerometer) ws;
        }
        return null;
    }
}