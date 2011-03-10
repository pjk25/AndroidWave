// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class WaveSensor {
    protected final String BASE_VERSION = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    public enum Type { ACCELEROMETER, MAGNETOMETER, LOCATION };
    
    protected static Set<WaveSensor> availableLocalSensors = null;
    
    protected Type type;
    
    protected WaveSensorChannel[] channels;
    
    /**
     * WaveSensor
     */
    public WaveSensor(Type t) {
        type = t;
    }
    
    /**
     * getType
     */
    public Type getType() {
        return type;
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
     * --------------------------- Static Methods ---------------------------
     */
    
    public static Set<WaveSensor> getAvailableLocalSensors(Context context) {
        
        if (availableLocalSensors == null) {
            Set<WaveSensor> sensors = new HashSet<WaveSensor>();
            
            SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
            Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d("WaveSensor", "getAvailableLocalSensors:");
            Log.d("WaveSensor", "\tsensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) => "+accelSensor);
            if (accelSensor != null) {
                // we have an accelerometer
                AndroidWaveSensor waveAccelSensor = new AndroidWaveSensor(Type.ACCELEROMETER);
                waveAccelSensor.androidSensor = accelSensor;
                // It will always have three channels in the current version
                // of the Android OS
                WaveSensorChannel[] channels = new WaveSensorChannel[3];
                channels[0] = new WaveSensorChannel("x");
                channels[0].units = "-m/s^2";
                channels[1] = new WaveSensorChannel("y");
                channels[1].units = channels[0].units;
                channels[2] = new WaveSensorChannel("z");
                channels[2].units = channels[0].units;
                
                waveAccelSensor.channels = channels;
                
                sensors.add(waveAccelSensor);
            }
            
            Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Log.d("WaveSensor", "getAvailableLocalSensors:");
            Log.d("WaveSensor", "\tsensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) => "+magSensor);
            if (magSensor != null) {
                AndroidWaveSensor waveMagSensor = new AndroidWaveSensor(Type.MAGNETOMETER);
                waveMagSensor.androidSensor = magSensor;
                // Always three channels in current Android OS version
                WaveSensorChannel[] channels = new WaveSensorChannel[3];
                channels[0] = new WaveSensorChannel("x");
                channels[0].units = "uT"; // micro-Tesla
                channels[1] = new WaveSensorChannel("y");
                channels[2].units = channels[0].units;
                channels[3] = new WaveSensorChannel("z");
                channels[3].units = channels[0].units;
                
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
    
    public static Set<WaveSensor> getAvailableLocalSensor(Context context, WaveSensor.Type type) {
        // null implementation
        return null;
    }
}