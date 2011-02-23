// 
//  WaveRecipeOutputChannel.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

public class WaveRecipeOutputChannel {
    
    protected String name;
    
    public WaveRecipeOutputChannel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}