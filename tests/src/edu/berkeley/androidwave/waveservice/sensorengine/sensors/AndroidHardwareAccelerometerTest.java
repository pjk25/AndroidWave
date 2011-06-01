// 
//  AndroidHardwareAccelerometerTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

// import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
// import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
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

    WaveSensorEvent firstEvent;
    int eventCount;
    
    WaveSensorListener waveSensorListener = new WaveSensorListener() {
        public void onWaveSensorChanged(WaveSensorEvent event) {
            // We store this event so that it can be asserted back on the test
            // thread
            synchronized(AndroidHardwareAccelerometerTest.this) {
                if (firstEvent == null) {
                    firstEvent = event;
                }
                eventCount++;
                AndroidHardwareAccelerometerTest.this.notify();
            }
        }
    };
    
    @Override
    public void setUp() {
        firstEvent = null;
        eventCount = 0;
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
    
    @SmallTest
    public void testStart() throws Exception {
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());
        
        synchronized(this) {
            fixtureOne.start(waveSensorListener, 5.0, 0.001);
        
            // start after start throws Exception
            try {
                fixtureOne.start(waveSensorListener, 6.0, 0.001);
            } catch (Exception e) {
                assertTrue(e instanceof Exception);
            }
        
            Log.d(TAG, "waiting for data...");
            this.wait(2*1000);  // wait up to 2 seconds
            Log.d(TAG, "checking for data");
            assertNotNull(firstEvent);
        }
    }
    
    @SmallTest
    public void testAlterRate() throws Exception {
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());

        // alter before start throws exception
        try {
            fixtureOne.alterRate(8.0);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        fixtureOne.start(waveSensorListener, 5.0, 0.001);
        fixtureOne.alterRate(8.0);
        
        // TODO: complete the test by checking the rate actually changes
    }
    
    @SmallTest
    public void testStop() throws Exception {
        AndroidHardwareAccelerometer fixtureOne = getFixtureOne(getContext());

        // stop without start throws exception
        try {
            fixtureOne.stop();
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        fixtureOne.start(waveSensorListener, 5.0, 0.001);
        fixtureOne.stop();
        
        // make sure data flow stops
        synchronized(this) {
            int c = eventCount;
            this.wait(1*1000); // wait 1 second for more data to show
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