// 
//  WaveRecipeOutputData.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WaveRecipeOutputData
 *
 * Meant to encapsulate the computed output from a recipe, so that it can be
 * sent out to the Wave Client app authorized for that recipe.
 */
public final class WaveRecipeOutputData implements Parcelable {
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel out, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeOutputData> CREATOR = new Parcelable.Creator<WaveRecipeOutputData>() {
        public WaveRecipeOutputData createFromParcel(Parcel in) {
            return new WaveRecipeOutputData(in);
        }
        
        public WaveRecipeOutputData[] newArray(int size) {
            return new WaveRecipeOutputData[size];
        }
    };
    
    private WaveRecipeOutputData(Parcel in) {
        
    }
}