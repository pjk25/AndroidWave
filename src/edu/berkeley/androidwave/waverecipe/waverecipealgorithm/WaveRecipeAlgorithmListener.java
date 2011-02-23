// 
//  WaveRecipeOutputDataListener.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-03.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

/**
 * WaveRecipeAlgorithmListener
 * 
 * Describes the receiver of a recipe's output, WaveRecipeOutputData. Provided
 * as an interface to simply what is presented to the recipe developer.
 */
public interface WaveRecipeAlgorithmListener {
    public void handleRecipeData(WaveRecipeOutputData data);
}