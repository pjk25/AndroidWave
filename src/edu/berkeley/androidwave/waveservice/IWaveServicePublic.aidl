// 
//  WaveServicePublic.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waveservice.IWaveRecipeOutputListener;

interface IWaveServicePublic {
    
    /**
     * Request recipe
     *
     * Request the use of a given recipe.  This should cause a switch to the
     * WAVE UI app, I believe through the use of an intent. Returns true if
     * the recipe is already authorized.
     */
    WaveRecipeAuthorization requestRecipe(in String recipeUID);
    
    /**
     * Register recipe output listener
     */
    boolean registerRecipeOutputListener(in IWaveRecipeOutputListener listener, boolean includeSensorData);
}