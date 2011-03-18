// 
//  WaveServicePublic.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waveservice.IWaveRecipeOutputDataListener;

interface IWaveServicePublic {
    
    /**
     * recipeExists
     * 
     * allows a client to check if a recipeID is valid and available to the
     * device
     */
    boolean recipeExists(in String recipeID);
    
    /**
     * isAuthorized
     * 
     * allows a client to check it if it remains authorized for a given
     * recipe, so in can launch the WAVE UI with an Intent if necessary
     */
    boolean isAuthorized(in String recipeID);
    
    /**
     * retreiveAuthorization
     * 
     * allows a client to retrieve details authorization info for a given
     * recipe
     */
    WaveRecipeAuthorization retrieveAuthorization(in String recipeID);
    
    /**
     * contstructs and appropriate Intent object with which a client can
     * launch the WAVE UI to authorize a recipe
     */
    Intent getAuthorizationIntent(in String recipeID);
    
    /**
     * Register recipe output listener
     */
    boolean registerRecipeOutputListener(in IWaveRecipeOutputDataListener listener, boolean includeSensorData);
}