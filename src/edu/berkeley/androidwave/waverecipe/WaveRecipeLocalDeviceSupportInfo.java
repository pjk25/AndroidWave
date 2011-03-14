// 
//  WaveRecipeLocalDeviceSupportInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

public class WaveRecipeLocalDeviceSupportInfo {
    
    protected boolean supported;
    
    public WaveRecipeLocalDeviceSupportInfo() {
        supported = false;
    }
    
    public boolean isSupported() {
        return supported;
    }
}