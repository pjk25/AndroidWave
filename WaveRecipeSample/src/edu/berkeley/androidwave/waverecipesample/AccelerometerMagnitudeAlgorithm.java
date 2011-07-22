// 
//  AccelerometerMagnitudeAlgorithm.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-03.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipesample;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.*;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class AccelerometerMagnitudeAlgorithm implements WaveRecipeAlgorithm {
    
    private static final String TAG = AccelerometerMagnitudeAlgorithm.class.getSimpleName();
    
    WaveRecipeAlgorithmListener theListener;  // <- this is actually a WaveRecipeAlgorithmListenerShadow instance
    
    public void setAuthorizedMaxOutputRate(double maxOutputRate) {
        // this recipe always outputs data at the input rate, so this method
        // does nothing
    }
    
    public boolean setWaveRecipeAlgorithmListener(Object listener) {
        // System.out.println("AccelerometerMagnitudeAlgorithm.setWaveRecipeAlgorithmListener("+listener+")");
        Log.d(TAG, "setWaveRecipeAlgorithmListener("+listener+")");
        try {
            theListener = new WaveRecipeAlgorithmListenerShadow(listener);
            return true;
        } catch (Exception e) {
            Log.w(TAG, "Exception in setWaveRecipeAlgorithmListener", e);
        }
        return false;
    }
    
    public void ingestSensorData(long time, Map<String, Double>values) {
        // System.out.println("AccelerometerMagnitudeAlgorithm.ingestSensorData("+sensorData+")");
        // Log.v(TAG, "ingestSensorData("+sensorData+")");
        try {
            // input values are in m/s^2
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            
            if (values.containsKey("x")) {
                x = values.get("x");
            }
            if (values.containsKey("y")) {
                y = values.get("y");
            }
            if (values.containsKey("z")) {
                z = values.get("z");
            }
            
            double mag = Math.hypot(x, y);
            mag = Math.hypot(mag, z);
            
            Map<String, Double> outValues = new HashMap<String, Double>(1);
            // output in g
            outValues.put("magnitude", mag / 9.81);
            
            theListener.handleRecipeData(time, outValues);
        } catch (Exception e) {
            Log.d(TAG, "Exception in ingestSensorData", e);
        }
    }
}