// 
//  SensorEngine.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SensorEngine implements SensorEventListener {
    
    protected static SensorEngine theInstance;
    
    protected Context mContext;
    
    protected SensorManager mSensorManager;
    
    protected HashMap<WaveSensor, Double> runningSensors;   // <- note not synchronized
    
    /**
     * Private Constructor for Singleton
     */
    private SensorEngine(Context c) {
        mContext = c;
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        runningSensors = new HashMap<WaveSensor, Double>();
    }
    
    public static void init(Context c) {
        theInstance = new SensorEngine(c);
    }
    
    public static SensorEngine getInstance() throws Exception {
        if (theInstance == null) {
            throw new Exception("SensorEngine.init not yet called.");
        }
        return theInstance;
    }
    
    /**
     * availableSensorsMatchingWaveSensorDescription
     * 
     * @see WaveSensor#getAvailableLocalSensors
     */
    public Set<WaveSensor> availableSensorsMatchingWaveSensorDescription(WaveSensorDescription sensorDescription)
            throws Exception {
        
        HashSet<WaveSensor> matchingSensors = new HashSet<WaveSensor>();
        
        Set<WaveSensor> availableLocalSensors = WaveSensor.getAvailableLocalSensors(mContext);
        
        for (WaveSensor candidateSensor : availableLocalSensors) {
            
            WaveSensor.Type targetType = sensorDescription.getType();
            if (candidateSensor.getType() == targetType) {
                if (sensorDescription.hasChannels()) {
                    // channel descriptions are present, so they must match
                    throw new Exception("not implemented yet");
                } else if (sensorDescription.hasExpectedUnits()) {
                    String expectedUnits = sensorDescription.getExpectedUnits();
                    // units description is present, so it must match
                    // this is tricky beacuse WaveSensor have units
                    // per/channel only
                    boolean andMatch = true;
                    // all WaveSensors should have at least one channel
                    for (WaveSensorChannel waveSensorChannel : candidateSensor.getChannels()) {
                        andMatch &= waveSensorChannel.units.equals(expectedUnits);
                    }
                    if (andMatch) {
                        matchingSensors.add(candidateSensor);
                    }
                } else {
                    matchingSensors.add(candidateSensor);
                }
            }
        }
        
        return matchingSensors;
    }
    
    /**
     * startAndroidWaveSensor
     * 
     * starts a sensor targeting a given sampling rate.  No scheduleing is
     * done here to support multiple recipes directly (hence a protected
     * method).  Not synchronized because it is not a public method.
     */
    protected void startAndroidWaveSensor(AndroidWaveSensor sensor, double rate) {
        mSensorManager.registerListener(this, sensor.getAndroidSensor(), SensorManager.SENSOR_DELAY_NORMAL);
        runningSensors.put(sensor, rate);
    }
    
    /**
     * stopAndroidWaveSensor
     * 
     * @see #startAndroidWaveSensor
     */
    protected boolean stopAndroidWaveSensor(AndroidWaveSensor sensor) {
        if (runningSensors.containsKey(sensor)) {
            runningSensors.remove(sensor);
            mSensorManager.unregisterListener(this);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * --------------------- SensorEventListener Methods ---------------------
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // null implementation
    }
    
    public void onSensorChanged(SensorEvent event) {
        // null implementation
    }
}