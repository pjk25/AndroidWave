// 
//  WaveRecipeOutput.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

/**
 * WaveRecipeOutput
 * 
 * General representation of one output from a WaveRecipe
 */
public class WaveRecipeOutput {
    
    protected String name;
    
    public WaveRecipeOutput(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}