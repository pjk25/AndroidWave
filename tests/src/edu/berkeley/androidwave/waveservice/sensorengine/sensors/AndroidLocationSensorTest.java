// 
//  AndroidLocationSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-09.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import java.util.Map;
import java.util.Set;

/**
 * AndroidLocationSensorTest
 * 
 * @see AndroidLocationSensor
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.sensorengine.sensors.AndroidLocationSensorTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class AndroidLocationSensorTest extends AndroidTestCase {
    
    private static final String TAG = AndroidLocationSensorTest.class.getSimpleName();
    
    // protected LocationManager locationManager;

    int eventCount;
    long lastTime;
    Map<String, Double> lastValues;

    class LstWaveRecipeAlgorithm implements WaveRecipeAlgorithm {
        
        public boolean setWaveRecipeAlgorithmListener(Object listener) {
            // return true so data will start to flow
            return true;
        }

        public void ingestSensorData(long time, Map<String, Double>values) {
            eventCount++;
            lastTime = time;
            lastValues = values;
            try {
                AndroidLocationSensorTest.this.notify();
            } catch (IllegalMonitorStateException imse) {}
        }
    }

    @Override
    public void setUp() {
        eventCount = 0;
        lastTime = 0;
        lastValues = null;
    }
    
    // @Override
    // public void setUp() {
        // firstEvent = null;
        // eventCount = 0;
        
        // set up a location provider for testing
        // locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        // 
        // locationManager.addTestProvider(AndroidLocationSensor.TEST_PROVIDER_NAME,     // <- name
        //                                 false,      // <- requiresNetwork
        //                                 false,      // <- requiresSatellite
        //                                 false,      // <- requiresCell
        //                                 false,      // <- hasMonetaryCost
        //                                 false,      // <- supportsAltitude
        //                                 false,      // <- supportsSpeed
        //                                 false,      // <- supportsBearing
        //                                 Criteria.POWER_LOW, // <- powerRequirement
        //                                 Criteria.ACCURACY_FINE); // <- accuracy
        // 
        // locationManager.setTestProviderEnabled(AndroidLocationSensor.TEST_PROVIDER_NAME, true);
        // 
        // locationManager.setTestProviderStatus(AndroidLocationSensor.TEST_PROVIDER_NAME, 
        //                                       LocationProvider.AVAILABLE,
        //                                       null,
        //                                       System.currentTimeMillis());
        // 
        // final long startTime = System.currentTimeMillis();
        // new Thread(new Runnable() {
        //     @Override
        //     public void run() {
        //         Location location = new Location("Test");
        //         long delta = System.currentTimeMillis() - startTime;
        //         location.setLatitude(delta / 100);
        //         location.setLongitude(20.0);
        //         locationManager.setTestProviderLocation(AndroidLocationSensor.TEST_PROVIDER_NAME, location);
        //         try {
        //             Thread.sleep(500);
        //         } catch (InterruptedException ie) {}
        //     }
        // }).start();
    // }
    
    @Override
    public void tearDown() {
        // locationManager.removeTestProvider(AndroidLocationSensor.TEST_PROVIDER_NAME);
    }

    /**
     * testInstancesAvailableInContext
     * 
     * Note: this test expects to be run on the simulator
     */
    @SmallTest
    public void testPreconditions() {
        Set<WaveSensor> sensors = AndroidLocationSensor.instancesAvailableInContext(getContext());
        assertNotNull(sensors);
        assertEquals(1, sensors.size());
        for (WaveSensor ws : sensors) {
            MoreAsserts.assertAssignableFrom(AndroidLocationSensor.class, ws);
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
        // test null locationManager throws exception
        try {
            new AndroidLocationSensor(getContext(), null);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
    
    @MediumTest
    public void testRegisterListener() throws Exception {
        
        if (Build.DEVICE.equals("generic") && Build.MODEL.equals("sdk")) {
            // this is the simulator, for which we don't test location updates
            return;
        }
        
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());
        
        WaveSensorDescription wsd = new WaveSensorDescription(WaveSensorDescription.Type.LOCATION, "degrees");
        
        WaveRecipeAlgorithm alg = new LstWaveRecipeAlgorithm();
        
        synchronized(this) {
            // successful start
            // 1Hz, 1m accuracy
            fixtureOne.registerListener(alg, wsd, 1.0, 1.0);
            
            // start after start throws exception
            try {
                fixtureOne.registerListener(alg, wsd, 1.0, 1.0);
            } catch (Exception e) {
                assertTrue(e instanceof Exception);
            }
            
            // data is received
            Log.d(TAG, "waiting for data...");
            this.wait(10*1000);    // wait up to 10 seconds
            Log.d(TAG, "checking for data");
            assertNotNull(lastValues);
        }
        
        // test stop
        fixtureOne.unregisterListener(alg);
        synchronized(this) {
            int c = eventCount;
            this.wait(3*1000);  // wait 3 seconds for more data
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
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());

        WaveSensorDescription sensorDescriptionOne = new WaveSensorDescription(WaveSensorDescription.Type.LOCATION, "degrees");
        
        assertTrue(fixtureOne.matchesWaveSensorDescription(sensorDescriptionOne));
    }
    
    /**
     * FIXTURES
     */
    
    public static AndroidLocationSensor getFixtureOne(Context c) {
        Set<WaveSensor> sensors = AndroidLocationSensor.instancesAvailableInContext(c);
        for (WaveSensor ws : sensors) {
            return (AndroidLocationSensor) ws;
        }
        return null;
    }
}