// 
//  WaveRecipeAlgorithm.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-02.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

public interface WaveRecipeAlgorithm {
    
    public boolean setWaveRecipeAlgorithmListener(WaveRecipeAlgorithmListener listener);
    
    public void ingestSensorData(WaveSensorData sensorData);
}