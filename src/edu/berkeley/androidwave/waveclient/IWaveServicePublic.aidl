// 
//  WaveServicePublic.aidl
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;

/**
 * IWaveServicePublic
 * 
 * Public interface to access the wave service.  We need to authenticate the
 * clients of this service individually.  For this purpose, a key must be
 * supplied with each call.  The caller generates a random key when they first
 * request an authorization.  The service stores that mapping during that
 * authorization.
 */
interface IWaveServicePublic {
    
    /**
     * isAuthorized
     * 
     * allows a client to check it if it remains authorized for a given
     * recipe, so in can launch the WAVE UI with an Intent if necessary. We
     * could also use {@code retrieveAuthorizationInfo}, but this method is
     * designed to be lighter.
     */
    boolean isAuthorized(in String key, in String recipeId);
    
    /**
     * retreiveAuthorization
     * 
     * allows a client to retrieve details authorization info for a given
     * recipe.  AuthorizationInfo is a limited Parcelable form of a
     * WaveRecipeAuthorization
     */
    WaveRecipeAuthorizationInfo retrieveAuthorizationInfo(in String key, in String recipeId);
    
    /**
     * contstructs and appropriate Intent object with which a client can
     * launch the WAVE UI to authorize a recipe
     */
    Intent getAuthorizationIntent(in String recipeId, in String key);
    
    /**
     * Register recipe output listener
     */
    boolean registerRecipeOutputListener(in String key, in String recipeId, in IWaveRecipeOutputDataListener listener);
    
    /**
     * Unregister recipe output listener
     */
    boolean unregisterRecipeOutputListener(in String key, in String recipeId);
}