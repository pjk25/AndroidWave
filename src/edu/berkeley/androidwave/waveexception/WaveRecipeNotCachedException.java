// 
//  WaveRecipeNotCachedException.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-04-20.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveexception;

public class WaveRecipeNotCachedException extends Exception {
    public WaveRecipeNotCachedException() {
        super();
    }
    
    public WaveRecipeNotCachedException(String detailMessage) {
        super(detailMessage);
    }
}