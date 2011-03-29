// 
//  WaveRecipeOutputDataImpl.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeOutputData;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WaveRecipeOutputDataImpl
 *
 * Meant to encapsulate the computed output from a recipe, so that it can be
 * sent out to the Wave Client app authorized for that recipe.
 */
public final class WaveRecipeOutputDataImpl implements WaveRecipeOutputData, Parcelable {
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel out, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeOutputDataImpl> CREATOR = new Parcelable.Creator<WaveRecipeOutputDataImpl>() {
        public WaveRecipeOutputDataImpl createFromParcel(Parcel in) {
            return new WaveRecipeOutputDataImpl(in);
        }
        
        public WaveRecipeOutputDataImpl[] newArray(int size) {
            return new WaveRecipeOutputDataImpl[size];
        }
    };
    
    private WaveRecipeOutputDataImpl(Parcel in) {
        
    }
}