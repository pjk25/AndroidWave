// 
//  WaveRecipeOutputListener.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-03.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import edu.berkeley.androidwave.waverecipe.WaveRecipeData;

public interface WaveRecipeAlgorithmListener {
    public void handleRecipeData(WaveRecipeData data);
}