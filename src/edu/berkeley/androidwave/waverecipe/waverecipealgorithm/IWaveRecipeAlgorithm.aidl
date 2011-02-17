// 
//  IWaveRecipeAlgorithm.aidl
//  WaveRecipeSample
//  
//  Created by Philip Kuryloski on 2011-02-16.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.IWaveRecipeAlgorithmListener;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

interface IWaveRecipeAlgorithm {
    
    boolean setWaveRecipeAlgorithmListener(in IWaveRecipeAlgorithmListener listener);
    
    void ingestSensorData(in WaveSensorData sensorData);
    
}