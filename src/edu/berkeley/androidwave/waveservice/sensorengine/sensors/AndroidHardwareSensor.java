// 
//  AndroidHardwareSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import java.util.Map;

/**
 * AndroidHardwareSensor
 * 
 * Subclass of {@link WaveSensor} specialized for sensors backed by an Android
 * OS sensor
 */
public abstract class AndroidHardwareSensor extends WaveSensor implements SensorEventListener {
    
    private static final String TAG = AndroidHardwareSensor.class.getSimpleName();
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    protected SensorManager mSensorManager;
    
    protected Sensor hardwareSensor;
    
    protected WaveSensorListener listener;
    
    protected boolean started;
    protected int sensorManagerRate;
    protected long lastSampleTime;
    protected double estimatedRate;
    
    public AndroidHardwareSensor(SensorManager sensorManager, String type, String units) {
        super(type, units);
        
        mSensorManager = sensorManager;
        started = false;
    }

    /**
     * returns a specific version string, which should be sufficient to
     * uniquely identify different sensor hardware/plugins
     */
    @Override
    public String getVersion() {
        return VERSION_BASE + "_" + this.getType();
    }
    
    /**
     * TODO: synchronize start/stop on something
     * 
     * Uses frequency mapping generated by
     * Mr. Charles Wang <charleswang007@gmail.com>
     * 
     * TODO: specify in microseconds file:///usr/local/android-sdk-mac_86/docs/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int)
     */
    @Override
    public void start(WaveSensorListener listener, double rateHint, double precisionHint) throws Exception {
        if (started) {
            throw new Exception("Sensor already started");
        }
        started = true;
        
        this.listener = listener;
        desiredRate = rateHint;
        
        /**
         * The documentation 
         * http://developer.android.com/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int)
         * suggests that the sensorManagerRate can be specified in mircrosend
         * delay between samples, however in practice it seems to always fail
         */
        // sensorManagerRate = (int) (1000.0 * 1000.0 / rateHint); // convert seconds to microseconds
        
        sensorManagerRate = SensorManager.SENSOR_DELAY_NORMAL;
        if (rateHint < 5.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_NORMAL;
        } else if (rateHint < 8.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_UI;
        } else if (rateHint < 12.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_GAME;
        } else {
            sensorManagerRate = SensorManager.SENSOR_DELAY_FASTEST;
        }
        
        if (!mSensorManager.registerListener(this, hardwareSensor, sensorManagerRate)) {
            throw new Exception("SensorManager.registerListener("+this+", "+hardwareSensor+", "+sensorManagerRate+") returned false");
        }
    }
    
    @Override
    public void alterRate(double newRate) throws Exception {
        throw new UnsupportedOperationException("alterRate not implemented");
    }
    
    @Override
    public void alterPrecision(double newPrecision) throws Exception {
        throw new UnsupportedOperationException("alterPrecision not implemented");
    }
    
    @Override
    public void stop() throws Exception {
        if (!started) {
            throw new Exception("Sensor has not been started yet");
        }
        
        mSensorManager.unregisterListener(this);
        
        started = false;
    }
    
    /**
     * Allow subclasses to name their channels, used when constructing
     * WaveSensorEvent
     */
    protected abstract Map<String, Double> sensorEventAsValues(SensorEvent event);
    
    /**
     * --------------------- SensorEventListener Methods ---------------------
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // null implementation
    }
    
    public void onSensorChanged(SensorEvent event) {
        // first update sensor stats for this sensor
        long last = lastSampleTime;
        lastSampleTime = event.timestamp;
        estimatedRate = 1000.0 / (event.timestamp - last);
        
        // dispatch the data to the listener
        listener.onWaveSensorChanged(new WaveSensorEvent(this, event.timestamp, sensorEventAsValues(event)));
        
        // adjust the rate if necessary
        if (estimatedRate < 0.9 * desiredRate) {
            // TODO: increase sensor rate
            Log.w(TAG, ""+event.sensor+" estimated rate less than 90% of desired rate ("+estimatedRate+" < 0.9 * "+desiredRate+")");
        }
    }
}