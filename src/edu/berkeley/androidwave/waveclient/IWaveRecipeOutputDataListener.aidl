// 
//  IWaveRecipeOutputDataListener.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.waveclient.WaveRecipeOutputDataImpl;

oneway interface IWaveRecipeOutputDataListener {
    
    /**
     * receiveWaveRecipeOutputData
     */
    void receiveWaveRecipeOutputData(in WaveRecipeOutputDataImpl wrOutput);
}