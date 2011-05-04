// 
//  SensorEngine.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waverecipe.WaveRecipeLocalDeviceSupportInfo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * SensorEngine
 * 
 * SensorEngine is a singleton, because there is only one set of underlying
 * hardware sensors.
 */
public class SensorEngine implements SensorEventListener {
    
    private static final String TAG = SensorEngine.class.getSimpleName();
    
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
    
    public static void init(Context c) { //throws Exception {
        if (theInstance != null) {
            Log.w(TAG, "SensorEngine.init() called more than once, dropping singleton instance "+theInstance);
            //throw new Exception("SensorEngine.init can only be called once");
        }
        theInstance = new SensorEngine(c);
    }
    
    /**
     * getInstance
     * 
     * Access the Singleton SensorEngine instance (without needing to supply
     * a context object)
     */
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
            
            WaveSensorDescription.Type targetType = sensorDescription.getType();
            if (candidateSensor.getType() == targetType) {
                if (sensorDescription.hasChannels()) {
                    // channel descriptions are present, so they must match
                    throw new Exception("not implemented yet");
                } else if (sensorDescription.hasExpectedUnits()) {
                    String expectedUnits = sensorDescription.getExpectedUnits();
                    if (candidateSensor.getUnits().equals(expectedUnits)) {
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
     * supportInfoForRecipe
     * 
     * provides information about the sensors which this device offers to
     * support a given recipe.  Currently if a device has multiple local
     * sensors of the same time, there is no particular order or ranking to
     * that portion of the matching.
     * 
     * TODO: Add support for conforming units, as currently they must match
     *       exactly
     */
    public WaveRecipeLocalDeviceSupportInfo supportInfoForRecipe(WaveRecipe recipe) {
        WaveRecipeLocalDeviceSupportInfo supportInfo = new WaveRecipeLocalDeviceSupportInfo(recipe);
        
        boolean allSensorsSatisfied = true;
        Set<WaveSensor> availableSensors = WaveSensor.getAvailableLocalSensors(mContext);
        // this is an inefficient inner loop, but the number of sensors is
        // expected to be small
        for (WaveSensorDescription wsd : recipe.getSensors()) {
            boolean thisSensorSatisfied = false;
            for (WaveSensor s : availableSensors) {
                if (s.matchesWaveSensorDescription(wsd)) {
                    thisSensorSatisfied = true;
                    // store information for this sensor in the supportInfo
                    supportInfo.getDescriptionToSensorMap().put(wsd, s);
                    break;
                }
            }
            allSensorsSatisfied &= thisSensorSatisfied;
        }
        
        supportInfo.setSupported(allSensorsSatisfied);
        
        return supportInfo;
    }
    
    public boolean scheduleAuthorization(WaveRecipeAuthorization authorization, WaveRecipeOutputListener listener) {
        // null implementation
        return false;
    }
    
    public boolean descheduleAuthorization(WaveRecipeAuthorization authorization) {
        // null implementation
        return false;
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