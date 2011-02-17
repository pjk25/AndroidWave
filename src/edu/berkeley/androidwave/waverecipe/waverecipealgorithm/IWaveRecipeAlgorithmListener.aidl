// 
//  IWaveRecipeAlgorithmListener.aidl
//  WaveRecipeSample
//  
//  Created by Philip Kuryloski on 2011-02-16.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeData;

interface IWaveRecipeAlgorithmListener {
    
    void handleRecipeData(in WaveRecipeData data);
    
}