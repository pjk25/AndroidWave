// 
//  WaveServicePublic.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;

interface IWaveServicePublic {
    
    /**
     * recipeExists
     * 
     * allows a client to check if a recipeID is valid and available to the
     * device. {@code search} indicates if the device may search external
     * recipe repositories.
     */
    boolean recipeExists(in String recipeID, boolean search);
    
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
     * recipe.  AuthorizationInfo is a limited Parcelable form of a
     * WaveRecipeAuthorization
     */
    WaveRecipeAuthorizationInfo retrieveAuthorizationInfo(in String recipeID);
    
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