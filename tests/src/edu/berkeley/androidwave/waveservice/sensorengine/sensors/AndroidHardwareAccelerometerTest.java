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

    int eventCount;
    long lastTime;
    Map<String, Double> lastValues;

    class HatWaveRecipeAlgorithm implements WaveRecipeAlgorithm {
        
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

    @Override
    public void setUp() {
        eventCount = 0;
        lastTime = 0;
        lastValues = null;
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
        // WaveRecipe recipeOne = WaveRecipeTest.getFixtureOne(getContext());
        // WaveRecipeLocalDeviceSupportInfo supportInfo = new WaveRecipeLocalDeviceSupportInfo(recipeOne);
        
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());
        
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.ACCELEROMETER, "-m/s^2");
        wsd.addChannel(new WaveSensorChannelDescription("x"));
        wsd.addChannel(new WaveSensorChannelDescription("y"));
        wsd.addChannel(new WaveSensorChannelDescription("z"));
        
        WaveRecipeAlgorithm alg = new HatWaveRecipeAlgorithm();
        
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
            assertNotNull(lastValues);
        }
        
        // test stop
        fixtureOne.unregisterListener(alg);
        synchronized(this) {
            int c = eventCount;
            this.wait(1*1000);  // wait 1 second for more data
            assertEquals("eventCount should stay same", c, eventCount);
        }
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