// 
//  WaveRecipeAlgorithm.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-02.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

/**
 * WaveRecipeAlgorithm
 *
 * Interface describing a WaveRecipeAlgorithm, which provides the
 * computational component of a WaveRecipe.  Recipe creators must implement
 * this interface, and provide that implementation within their recipe.
 * 
 * Arguments are generic objects, because the dynamic loading used to
 * instantiate this algorithm in Android results in two separate namespaces.
 * Therefore, we essentially cannot cast or type easily at compile time.
 * 
 * For conviencience, recipe developers are provided with
 * WaveRecipeAlgorithmListenerShadow and WaveSensorDataShadow, which
 * essentially provide a runtime cast.
 * 
 * TODO: make a generic shadow class
 */
public interface WaveRecipeAlgorithm {
    
    /**
     * setWaveRecipeAlgorithmListener
     * 
     * @param listener should implement the WaveRecipeAlgorithmListener interface
     */
    public boolean setWaveRecipeAlgorithmListener(Object listener);
    
    public void ingestSensorData(Object sensorData);
}