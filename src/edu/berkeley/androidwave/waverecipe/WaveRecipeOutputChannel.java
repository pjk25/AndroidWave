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
    protected String units;
    
    public WaveRecipeOutputChannel(String name, String units) {
        this.name = name;
        this.units = units;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUnits() {
        return units;
    }
}