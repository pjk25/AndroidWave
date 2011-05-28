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
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
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

    WaveSensorListener waveSensorListener = new WaveSensorListener() {
        public void onWaveSensorChanged(WaveSensorEvent event) {
            // nothing for now
        }
    };
    
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
            new AndroidLocationSensor(null);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
    
    @SmallTest
    public void testStart() throws Exception {
        AndroidLocationSensor fixtureOne = getFixtureOne(getContext());
        
        // stop before start throws Exception
        try {
            fixtureOne.stop();
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        fixtureOne.start(waveSensorListener, 5.0);
        
        // start after start throws Exception
        try {
            fixtureOne.start(waveSensorListener, 6.0);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
        
        // TODO: complete the test by checking that data starts flowing
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
        
        fixtureOne.start(waveSensorListener, 5.0);
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
        
        fixtureOne.start(waveSensorListener, 5.0);
        fixtureOne.stop();
        
        // TODO: complete the test by checking that data flow actually stops
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