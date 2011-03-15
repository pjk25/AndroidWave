// 
//  WaveRecipeLocalDeviceSupportInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-14.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import java.util.HashMap;

public class WaveRecipeLocalDeviceSupportInfo {
    
    protected boolean supported;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxRateMap;
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxPrecisionMap;
    
    public WaveRecipeLocalDeviceSupportInfo() {
        supported = false;
        
        sensorDescriptionMaxRateMap = new HashMap<WaveSensorDescription, Double>();
        sensorDescriptionMaxPrecisionMap = new HashMap<WaveSensorDescription, Double>();
        sensorChannelDescriptionMaxRateMap = new HashMap<WaveSensorChannelDescription, Double>();
        sensorChannelDescriptionMaxPrecisionMap = new HashMap<WaveSensorChannelDescription, Double>();
    }
    
    public boolean isSupported() {
        return supported;
    }

    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxRateMap() {
        return sensorDescriptionMaxRateMap;
    }
    
    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxPrecisionMap() {
        return sensorDescriptionMaxPrecisionMap;
    }
    
    public HashMap<WaveSensorChannelDescription, Double> getSensorChannelDescriptionMaxRateMap() {
        return sensorChannelDescriptionMaxRateMap;
    }
    
    public HashMap<WaveSensorChannelDescription, Double> getSensorChannelDescriptionMaxPrecisionMap() {
        return sensorChannelDescriptionMaxPrecisionMap;
    }
}