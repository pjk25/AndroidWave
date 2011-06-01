// 
//  AndroidLocationSensorTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-09.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
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
    
    protected LocationManager locationManager;

    private WaveSensorEvent firstEvent;
    private int eventCount;
    
    WaveSensorListener waveSensorListener = new WaveSensorListener() {
        public void onWaveSensorChanged(WaveSensorEvent event) {
            // We store this event so that it can be asserted back on the test
            // thread
            synchronized(AndroidLocationSensorTest.this) {
                if (firstEvent == null) {
                    firstEvent = event;
                }
                eventCount++;
                AndroidLocationSensorTest.this.notify();
            }
        }
    };
    
    @Override
    public void setUp() {
        firstEvent = null;
        eventCount = 0;
        
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
    }
    
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

    @SmallTest
    public void testConstructor() {
        // test null locationManager throws exception
        try {
            new AndroidLocationSensor(getContext(), null);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
    
    @SmallTest
    public void testStart() throws Exception {
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());
        
        synchronized(this) {
            // use a precision hint of 500, as it is less than the GPS activation threshold
            fixtureOne.start(waveSensorListener, 5.0, 500);
        
            // start after start throws Exception
            try {
                fixtureOne.start(waveSensorListener, 6.0, 500);
            } catch (Exception e) {
                assertTrue(e instanceof Exception);
            }
        
            // Log.d(TAG, "waiting for data...");
            // this.wait(20*1000);  // wait up to 20 seconds
            // Log.d(TAG, "checking for data");
            // assertNotNull(firstEvent);
        }
    }
    
    @SmallTest
    public void testAlterRate() throws Exception {
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());

        // alter before start throws exception
        try {
            fixtureOne.alterRate(8.0);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        fixtureOne.start(waveSensorListener, 5.0, 500);
        fixtureOne.alterRate(8.0);
        
        // TODO: complete the test by checking the rate actually changes
    }
    
    @SmallTest
    public void testStop() throws Exception {
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());

        // stop without start throws exception
        try {
            fixtureOne.stop();
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        fixtureOne.start(waveSensorListener, 5.0, 500);
        fixtureOne.stop();
        
        // make sure data flow stops
        synchronized(this) {
            int c = eventCount;
            this.wait(3*1000); // wait 3 seconds for more data
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