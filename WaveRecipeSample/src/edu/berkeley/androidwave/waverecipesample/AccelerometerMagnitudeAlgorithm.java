// 
//  AccelerometerMagnitudeAlgorithm.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-03.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipesample;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithmListener;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithmListenerShadow;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorDataShadow;

public class AccelerometerMagnitudeAlgorithm implements WaveRecipeAlgorithm {
    
    public boolean setWaveRecipeAlgorithmListener(Object listener) {
        try {
            WaveRecipeAlgorithmListener theListener = new WaveRecipeAlgorithmListenerShadow(listener);
        } catch (Exception e) {
            
        }
        // null implementation
        return false;
    }
    
    public void ingestSensorData(Object sensorData) {
        try {
            WaveSensorData theSensorData = new WaveSensorDataShadow(sensorData);
        } catch (Exception e) {
            
        }
        // null implementation
    }
}