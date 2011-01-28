// 
//  WaveRecipeAuthorization.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-26.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WaveRecipeAuthorization
 * 
 * An "instance" of a recipe, as used by client applications. It points to the
 * original verified recipe, but allows multiple client apps to use the same
 * recipe.
 */
public class WaveRecipeAuthorization implements Parcelable {
    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeAuthorization> CREATOR = new Parcelable.Creator<WaveRecipeAuthorization>() {
        public WaveRecipeAuthorization createFromParcel(Parcel in) {
            return new WaveRecipeAuthorization(in);
        }
        
        public WaveRecipeAuthorization[] newArray(int size) {
            return new WaveRecipeAuthorization[size];
        }
    };
    
    private WaveRecipeAuthorization(Parcel in) {
        
    }
}