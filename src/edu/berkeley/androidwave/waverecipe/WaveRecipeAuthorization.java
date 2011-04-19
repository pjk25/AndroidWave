// 
//  WaveRecipeAuthorization.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-26.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waverecipe.granularitytable.GranularityTable;

import android.content.pm.Signature;
import android.util.Log;
import java.util.HashMap;

/**
 * WaveRecipeAuthorization
 * 
 * An "instance" of a recipe, as used by client applications. It points to the
 * original verified recipe, but allows multiple client apps to use the same
 * recipe.
 */
public class WaveRecipeAuthorization {
    
    private static final String TAG = WaveRecipeAuthorization.class.getSimpleName();
    
    protected WaveRecipe recipe;
    
    protected WaveRecipeLocalDeviceSupportInfo supportInfo;
    
    protected String recipeClientName;
    protected Signature[] recipeClientSignatures;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    public WaveRecipeAuthorization(WaveRecipeLocalDeviceSupportInfo supportInfo) {
        this.supportInfo = supportInfo;
        this.recipe = supportInfo.getAssociatedRecipe();

        sensorDescriptionMaxRateMap = new HashMap<WaveSensorDescription, Double>();
        sensorDescriptionMaxPrecisionMap = new HashMap<WaveSensorDescription, Double>();
    }
    
    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxRateMap() {
        return sensorDescriptionMaxRateMap;
    }
    
    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxPrecisionMap() {
        return sensorDescriptionMaxPrecisionMap;
    }
    
    public WaveRecipeAuthorizationInfo asInfo() {
        WaveRecipeAuthorizationInfo info = new WaveRecipeAuthorizationInfo(recipe.getId());
        
        info.recipeOutputDescription = recipe.getOutput();
        
        GranularityTable t = recipe.getGranularityTable();
        try {
            info.outputMaxRate = t.rateForSensorRates(sensorDescriptionMaxRateMap);
            info.outputMaxPrecision = t.precisionForSensorPrecisions(sensorDescriptionMaxPrecisionMap);
        } catch (Exception e) {
            Log.w(TAG, "Exception raised while calculating output rate and precision", e);
        }
        
        return info;
    }
}