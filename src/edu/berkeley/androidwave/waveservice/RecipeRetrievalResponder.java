// 
//  RecipeRetrievalResponder.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-04-06.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import java.io.File;

/**
 * RecipeRetrievalResponder
 * 
 * Interface adopted by an Activity for receiving notification that a recipe
 * download by WaveService has succeeded or failed
 */
public interface RecipeRetrievalResponder {
    
    public void handleRetrievalFailed(String recipeId, String message);
    
    public void handleRetrievalFinished(String recipeId, File f);
}