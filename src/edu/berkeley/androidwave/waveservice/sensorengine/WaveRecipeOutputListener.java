// 
//  WaveRecipeOutputListener.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-04.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;

import java.util.Map;

public interface WaveRecipeOutputListener {
    public void receiveDataForAuthorization(long time, Map<String, Double> values, WaveRecipeAuthorization authorization);
}