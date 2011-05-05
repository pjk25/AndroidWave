// 
//  WaveRecipeOutputListener.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-04.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeOutputData;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;

public interface WaveRecipeOutputListener {
    public void receiveDataForAuthorization(WaveRecipeOutputData data, WaveRecipeAuthorization authorization);
}