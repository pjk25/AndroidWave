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

/**
 * WaveRecipeOutputListener
 * 
 * Currently only implemented by WaveService, the interface through which
 * data is sent from an AlgorithmOutputForwarder instance in the SensorEngine
 * to WaveService, at which point WaveService dispatches it over IPC to the
 * appropriate client. The WaveRecipeAuthorization argument serves as the
 * address for that dispatch.
 */
public interface WaveRecipeOutputListener {
    public void receiveDataForAuthorization(long time, Map<String, Double> values, WaveRecipeAuthorization authorization);
}