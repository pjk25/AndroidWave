// 
//  WaveServicePublic.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.calfitwave.waveservice;

import edu.berkeley.calfitwave.waverecipe.WaveRecipe;
import edu.berkeley.calfitwave.waveservice.IWaveRecipeOutputListener;

interface IWaveServicePublic {
    
    /**
     * Register Recipe
     *
     * An app needs a way to register a new recipe it intends to use
     */
    boolean registerRecipe(in WaveRecipe recipe);
    
    /**
     * Request recipe
     *
     * Request the use of a given recipe.  This should cause a switch to the
     * WAVE UI app, I believe through the use of an intent. Returns true if
     * the recipe is already authorized.
     */
    WaveRecipe requestRecipe(in String recipeUID);
    
    /**
     * Register recipe output listener
     */
    boolean registerRecipeOutputListener(in IWaveRecipeOutputListener listener, boolean includeSensorData);
}