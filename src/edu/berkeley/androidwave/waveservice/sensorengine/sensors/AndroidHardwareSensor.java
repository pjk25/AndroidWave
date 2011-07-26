// 
//  AndroidHardwareSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
//  
//  Uses portions by Mr. Charles Wang <charleswang007@gmail.com>
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * AndroidHardwareSensor
 * 
 * Subclass of {@link WaveSensor} specialized for sensors backed by an Android
 * OS sensor
 */
public abstract class AndroidHardwareSensor extends WaveSensor {
    
    private static final String TAG = AndroidHardwareSensor.class.getSimpleName();
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    protected SensorManager mSensorManager;
    
    protected Sensor hardwareSensor;
    
    class SensorDataForwarder implements SensorEventListener {
        int sampleCount;
        int droppedSampleCount;
        int exceptionCount;
        
        long lastForwardTime;
        int sensorManagerRate;
        long minOutputInterval; // in nanoseconds
        double maxOutputPrecision;
        WaveSensorDescription authorizedDescription;
        WaveRecipeAlgorithm destination;
        
        Looper looper;
        
        SensorDataForwarder(int smr, double rate, double precision, WaveSensorDescription wsd, WaveRecipeAlgorithm dest, Looper l) {
            sampleCount = 0;
            droppedSampleCount = 0;
            exceptionCount = 0;
            
            sensorManagerRate = smr;
            lastForwardTime = 0;
            minOutputInterval = (long) (1000.0 * 1000.0 * 1000.0 / rate);
            maxOutputPrecision = precision;
            authorizedDescription = wsd;
            destination = dest;
            looper = l;
        }

        /**
         * --------------------- SensorEventListener Methods ---------------------
         */
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // null implementation
        }

        public void onSensorChanged(SensorEvent event) {
            sampleCount++;
            
            long interval = event.timestamp - lastForwardTime;
            if (interval >= 0.9 * minOutputInterval) {
                lastForwardTime = event.timestamp;
                
                Map<String, Double> values;
                // for now simple channel handling
                // TODO: better channel handling
                WaveSensorChannelDescription[] wscds = authorizedDescription.getChannels();
                if (wscds.length > 0) {
                    values = new HashMap<String, Double>();
                    for (WaveSensorChannelDescription wscd : wscds) {
                        String name = wscd.getName();
                        values.put(name, sensorEventQuantizedChannel(event, name, maxOutputPrecision));
                    }
                } else {
                    // no channels specified, send all
                    values = sensorEventQuantized(event, maxOutputPrecision);
                }
                assert values.size() > 0;
                
                // call up the algorithmInstance of the authorization
                // TODO: call ingestSensorData on different thread
                try {
                    destination.ingestSensorData(event.timestamp, values);
                } catch (Exception e) {
                    exceptionCount++;
                    if (exceptionCount < 10) {
                        Log.d(TAG, "Exception in recipe algorithm: "+e);
                    } else if (exceptionCount > 50) {
                        Log.w(TAG, "Excessive (> 50) exceptions in destination.ingestSensorData, stopping sensor.");
                        try {
                            unregisterListener(destination);
                        } catch (Exception se) {
                            Log.w(TAG, "Exception while attempting to cancel sensor due to excessive exceptions in associated listener", se);
                        }
                    }
                }
            } else {
                droppedSampleCount++;
                // Log.v(TAG, String.format("Excessive sensor data dropped (interval => %d, minOutputInterval => %d)", interval, minOutputInterval));
            }
        }
    }
    
    Map<WaveRecipeAlgorithm, SensorDataForwarder> forwarderMap;
    
    /**
     * Constructor
     */
    public AndroidHardwareSensor(SensorManager sensorManager, String type, String units) {
        super(type, units);
        
        mSensorManager = sensorManager;
        forwarderMap = new HashMap<WaveRecipeAlgorithm, SensorDataForwarder>();
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
     * 
     */
    @Override
    public synchronized void registerListener(WaveRecipeAlgorithm listener, WaveSensorDescription wsd, double rateHint, double precisionHint)
            throws Exception {
        if (forwarderMap.containsKey(listener)) {
            throw new Exception(""+listener+" already registered.");
        }
        
        incrementActiveCount();
        
        /**
         * Frequency mapping below generated by
         * Mr. Charles Wang <charleswang007@gmail.com>
         */
        int sensorManagerRate;
        if (rateHint < 5.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_NORMAL;
        } else if (rateHint < 8.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_UI;
        } else if (rateHint < 12.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_GAME;
        } else {
            sensorManagerRate = SensorManager.SENSOR_DELAY_FASTEST;
        }
        
        final int finalSensorManagerRate = sensorManagerRate;
        final double finalRateHint = rateHint;
        final double finalPrecisionHint = precisionHint;
        final WaveSensorDescription finalWsd = wsd;
        final WaveRecipeAlgorithm finalListener = listener;
        
        new Thread(new Runnable() {
            public void run() {
                
                Looper.prepare();
                
                SensorDataForwarder sdf = new SensorDataForwarder(finalSensorManagerRate,
                                                                  finalRateHint,
                                                                  finalPrecisionHint,
                                                                  finalWsd,
                                                                  finalListener,
                                                                  Looper.myLooper());
                
                // TODO: forwarderMap should be a synchronized collection?
                forwarderMap.put(finalListener, sdf);
                
                if (!mSensorManager.registerListener(sdf, hardwareSensor, finalSensorManagerRate)) {
                    Log.d(TAG, "SensorManager.registerListener("+sdf+", "+hardwareSensor+", "+finalSensorManagerRate+") returned false");
                    // TODO: signal this failure in the registerListener method
                    return;
                }
                
                // Looper.loop() is required to keep this thread alive so that location
                // updates are actually delivered
                Looper.loop();
                Log.d(TAG, "Looper.loop() finished");
            }
        }).start();
    }
    
    /**
     * 
     */
    @Override
    public synchronized void unregisterListener(WaveRecipeAlgorithm listener)
            throws Exception {
        if (!forwarderMap.containsKey(listener)) {
            throw new Exception(""+listener+" not yet registered.");
        }
        
        SensorDataForwarder sdf = forwarderMap.get(listener);
        Log.d(TAG, "unregisterListener("+listener+"), droppedSampleRatio => "+(1.0*sdf.droppedSampleCount/sdf.sampleCount));
        mSensorManager.unregisterListener(sdf);
        sdf.looper.quit();
        forwarderMap.remove(listener);
        
        // make sure that any wake locks are released if necessary
        decrementActiveCount();
    }
    
    /**
     * Allow subclasses to name their channels, used when constructing
     * WaveSensorEvent
     */
    protected abstract double sensorEventQuantizedChannel(SensorEvent event, String channelName, double precision);
    
    protected abstract Map<String, Double> sensorEventQuantized(SensorEvent event, double precision);
}