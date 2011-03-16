// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WaveSensor {
    protected final String BASE_VERSION = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    public enum Type { ACCELEROMETER, MAGNETOMETER, LOCATION };
    
    protected static Context mContext;
    protected static Set<WaveSensor> availableLocalSensors = null;
    
    protected Type type;
    protected String units;
    
    protected WaveSensorChannel[] channels;
    
    /**
     * WaveSensor
     */
    public WaveSensor(Type t, String units) {
        type = t;
        this.units = units;
    }
    
    /**
     * getType
     */
    public Type getType() {
        return type;
    }
    
    /**
     * getUnits
     */
    public String getUnits() {
        return units;
    }
    
    /**
     * getVersion
     *
     * should allow identification of sensor hardware
     */
    public String getVersion() {
        return BASE_VERSION;
    }
    
    /**
     * getMaximumAvailablePrecision
     * 
     * currently hard coded.  Might need a lookup table based on device
     * model
     */
    public double getMaximumAvailablePrecision() {
        // assume sensor reports binary thousands of a g
        return (9.81/1024.0);
    }
    
    /**
     * getMaximumAvailableSamplingFrequency
     */
    public double getMaximumAvailableSamplingFrequency() {
        // assume 10Hz
        return 10.0;
    }
    
    /**
     * getChannels
     * 
     * @see WaveSensorChannel
     */
    public WaveSensorChannel[] getChannels() {
        return channels;
    }
    
    /**
     * return the names of this sensor's channels as an ArrayList
     */
    private ArrayList<String> getChannelNamesArrayList() {
        WaveSensorChannel[] theseChannels = this.getChannels();
        ArrayList<String> theseChannelNames = new ArrayList<String>(theseChannels.length);
        for (int i=0; i<theseChannels.length; i++) {
            theseChannelNames.add(theseChannels[i].getName());
        }
        return theseChannelNames;
    }
    
    /**
     * indicates if this wavesensor is in fact a "match" for a
     * {@code WaveSensorDescription} object used in a WaveRecipe
     */
    public boolean matchesWaveSensorDescription(WaveSensorDescription wsd) {
        boolean doesMatch = true;
        
        doesMatch &= (this.getType() == wsd.getType()); // "==" is okay because we are comparing enums
        if (wsd.hasExpectedUnits()) {
            doesMatch &= (this.getUnits().equals(wsd.getExpectedUnits()));
        }
        
        if (wsd.hasChannels()) {
            // construct an ArrayList of the wsd's channels
            WaveSensorChannelDescription[] wsdChannels = wsd.getChannels();
            ArrayList<String> wsdChannelNames = new ArrayList<String>(wsdChannels.length);
            for (int i=0; i<wsdChannels.length; i++) {
                wsdChannelNames.add(wsdChannels[i].getName());
            }
            
            // available channel names should be a superset of the description
            // channel names
            doesMatch &= (this.getChannelNamesArrayList().containsAll(wsdChannelNames));
        }
        return doesMatch;
    }
    
    /**
     * --------------------------- Static Methods ---------------------------
     */
    
    public static Set<WaveSensor> getAvailableLocalSensors(Context context) {
        
        if ((availableLocalSensors == null) || (context != mContext)) {
            Log.d("WaveSensor", "cache miss on getAvailableLocalSensors");
            mContext = context;
            Set<WaveSensor> sensors = new HashSet<WaveSensor>();
            
            SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
            Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d("WaveSensor", "getAvailableLocalSensors:");
            Log.d("WaveSensor", "\tsensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) => "+accelSensor);
            if (accelSensor != null) {
                // we have an accelerometer
                AndroidWaveSensor waveAccelSensor = new AndroidWaveSensor(Type.ACCELEROMETER, "-m/s^2");
                waveAccelSensor.androidSensor = accelSensor;
                // It will always have three channels in the current version
                // of the Android OS
                WaveSensorChannel[] channels = new WaveSensorChannel[3];
                channels[0] = new WaveSensorChannel("x");
                channels[1] = new WaveSensorChannel("y");
                channels[2] = new WaveSensorChannel("z");
                waveAccelSensor.channels = channels;
                
                sensors.add(waveAccelSensor);
            }
            
            Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Log.d("WaveSensor", "getAvailableLocalSensors:");
            Log.d("WaveSensor", "\tsensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) => "+magSensor);
            if (magSensor != null) {
                AndroidWaveSensor waveMagSensor = new AndroidWaveSensor(Type.MAGNETOMETER, "uT"); // micro-Tesla
                waveMagSensor.androidSensor = magSensor;
                // Always three channels in current Android OS version
                WaveSensorChannel[] channels = new WaveSensorChannel[3];
                channels[0] = new WaveSensorChannel("x");
                channels[1] = new WaveSensorChannel("y");
                channels[3] = new WaveSensorChannel("z");
                waveMagSensor.channels = channels;
                
                sensors.add(waveMagSensor);
            }
            
            LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            Log.d("WaveSensor", "getAvailableLocalSensors:");
            Log.d("WaveSensor", "\tcontext.getSystemService(Context.LOCATION_SERVICE) => "+locationManager);
            if (locationManager != null) {
                // LocationWaveSensor is very specialized, and so should do
                // most of it's setup internally
                LocationWaveSensor waveLocSensor = new LocationWaveSensor(context);
                sensors.add(waveLocSensor);
            }
            
            availableLocalSensors = sensors;
        }
        
        return availableLocalSensors;
    }
    
    /**
     * getAvailableLocalSensor
     * 
     * Similar to @see #getAvailableLocalSensors, but filters by sensor type
     */
    public static Set<WaveSensor> getAvailableLocalSensor(Context context, WaveSensor.Type type) {
        
        Set<WaveSensor> availableLocalSensors = getAvailableLocalSensors(context);
        Set<WaveSensor> resultSet = new HashSet<WaveSensor>();
        
        /**
         * TODO: consider optimizing this implementation by building a Map
         * which caches Sets of sensors by type for quick retrieval
         */
        
        for (WaveSensor s : availableLocalSensors) {
            if (s.getType() == type) {
                resultSet.add(s);
            }
        }
        
        return resultSet;
    }
}