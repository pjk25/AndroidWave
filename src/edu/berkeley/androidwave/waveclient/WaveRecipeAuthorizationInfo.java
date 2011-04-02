// 
//  WaveRecipeAuthorizationInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-28.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * WaveRecipeAuthorization
 * 
 * Pacelable version of {@link edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization}
 */
public class WaveRecipeAuthorizationInfo implements Parcelable {
    
    protected String recipeId;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxRateMap;
    protected HashMap<WaveSensorChannelDescription, Double> sensorChannelDescriptionMaxPrecisionMap;
    
    public WaveRecipeAuthorizationInfo() {
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

    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeAuthorizationInfo> CREATOR = new Parcelable.Creator<WaveRecipeAuthorizationInfo>() {
        public WaveRecipeAuthorizationInfo createFromParcel(Parcel in) {
            return new WaveRecipeAuthorizationInfo(in);
        }
        
        public WaveRecipeAuthorizationInfo[] newArray(int size) {
            return new WaveRecipeAuthorizationInfo[size];
        }
    };
    
    private WaveRecipeAuthorizationInfo(Parcel in) {
        
    }
}