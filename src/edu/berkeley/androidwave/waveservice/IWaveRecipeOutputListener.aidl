// 
//  IWaveRecipeOutputListener.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.calfitwave.waveservice;

//parcelable WaveRecipeOutput;

import edu.berkeley.calfitwave.waverecipe.WaveRecipeOutput;

oneway interface IWaveRecipeOutputListener {
    
    /**
     * receiveWaveRecipeOutput
     */
    void receiveWaveRecipeOutput(in WaveRecipeOutput wrOutput);
}