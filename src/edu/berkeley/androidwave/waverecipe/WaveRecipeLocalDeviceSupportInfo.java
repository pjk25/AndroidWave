// 
//  WaveRecipeLocalDeviceSupportInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import java.util.HashMap;

/**
 * WaveRecipeLocalDeviceSupportInfo
 * 
 * @see WaveRecipeAuthorization
 */
public class WaveRecipeLocalDeviceSupportInfo {
    
    protected boolean supported;
    
    protected HashMap<WaveSensorDescription, WaveSensor> descriptionToSensorMap;
    
    public WaveRecipeLocalDeviceSupportInfo() {
        supported = false;
        
        descriptionToSensorMap = new HashMap<WaveSensorDescription, WaveSensor>();
    }
    
    public boolean isSupported() {
        return supported;
    }
    
    public void setSupported(boolean supported) {
        this.supported = supported;
    }
    
    public HashMap<WaveSensorDescription, WaveSensor> getDescriptionToSensorMap() {
        return descriptionToSensorMap;
    }
}