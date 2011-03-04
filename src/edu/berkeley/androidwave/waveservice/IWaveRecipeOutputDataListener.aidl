// 
//  IWaveRecipeOutputDataListener.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeOutputData;

oneway interface IWaveRecipeOutputDataListener {
    
    /**
     * receiveWaveRecipeOutputData
     */
    void receiveWaveRecipeOutputData(in WaveRecipeOutputData wrOutput);
}