// 
//  WaveRecipeLocalDeviceSupportInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.sensors.WaveSensor;

import java.util.HashMap;
import java.util.Map;

/**
 * WaveRecipeLocalDeviceSupportInfo
 * 
 * Provides a mapping between sensors described in a recipe to local sensors
 * available on the device
 * 
 * @see WaveRecipeAuthorization
 */
public class WaveRecipeLocalDeviceSupportInfo {
    
    protected WaveRecipe associatedRecipe;
    
    protected boolean supported;
    
    protected Map<WaveSensorDescription, WaveSensor> descriptionToSensorMap;
    
    public WaveRecipeLocalDeviceSupportInfo(WaveRecipe recipe) {
        associatedRecipe = recipe;
        
        supported = false;
        
        descriptionToSensorMap = new HashMap<WaveSensorDescription, WaveSensor>();
    }
    
    public WaveRecipe getAssociatedRecipe() {
        return associatedRecipe;
    }
    
    public boolean isSupported() {
        return supported;
    }
    
    public void setSupported(boolean supported) {
        this.supported = supported;
    }
    
    public Map<WaveSensorDescription, WaveSensor> getDescriptionToSensorMap() {
        return descriptionToSensorMap;
    }
}