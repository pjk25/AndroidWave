// 
//  WaveRecipeAuthorization.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-26.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveclient.WaveSensorDescription;
import edu.berkeley.androidwave.waveclient.WaveSensorChannelDescription;

import android.content.pm.Signature;

import java.util.HashMap;

/**
 * WaveRecipeAuthorization
 * 
 * An "instance" of a recipe, as used by client applications. It points to the
 * original verified recipe, but allows multiple client apps to use the same
 * recipe.
 */
public class WaveRecipeAuthorization {
    
    protected WaveRecipe recipe;
    
    protected WaveRecipeLocalDeviceSupportInfo supportInfo;
    
    protected String recipeClientName;
    protected Signature[] recipeClientSignatures;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxRateMap;
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxPrecisionMap;
    
    public WaveRecipeAuthorization(WaveRecipeLocalDeviceSupportInfo supportInfo) {
        this.supportInfo = supportInfo;
        this.recipe = supportInfo.getAssociatedRecipe();

        sensorDescriptionMaxRateMap = new HashMap<WaveSensorDescription, Double>();
        sensorDescriptionMaxPrecisionMap = new HashMap<WaveSensorDescription, Double>();
        sensorChannelDescriptionMaxRateMap = new HashMap<WaveSensorChannelDescription, Double>();
        sensorChannelDescriptionMaxPrecisionMap = new HashMap<WaveSensorChannelDescription, Double>();
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
    
    public WaveRecipeAuthorizationInfo asInfo() {
        return null;
    }
}