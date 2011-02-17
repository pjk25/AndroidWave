// 
//  WaveRecipeData.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-07.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import android.os.Parcel;
import android.os.Parcelable;

public class WaveRecipeData {
    
    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeData> CREATOR = new Parcelable.Creator<WaveRecipeData>() {
        public WaveRecipeData createFromParcel(Parcel in) {
            return new WaveRecipeData(in);
        }
        
        public WaveRecipeData[] newArray(int size) {
            return new WaveRecipeData[size];
        }
    };
    
    private WaveRecipeData(Parcel in) {
        
    }
}