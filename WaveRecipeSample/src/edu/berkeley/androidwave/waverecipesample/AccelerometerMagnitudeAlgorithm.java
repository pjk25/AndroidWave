// 
//  AccelerometerMagnitudeAlgorithm.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-03.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipesample;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.IWaveRecipeAlgorithm;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.IWaveRecipeAlgorithmListener;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccelerometerMagnitudeAlgorithm extends Service {
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    private final IWaveRecipeAlgorithm.Stub mBinder = new IWaveRecipeAlgorithm.Stub() {
        
        public boolean setWaveRecipeAlgorithmListener(IWaveRecipeAlgorithmListener listener) {
            // null implementation
            return false;
        }

        public void ingestSensorData(WaveSensorData sensorData) {
            // null implementation
        }
    };
}