// 
//  WaveRecipeOutput.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.calfitwave.waverecipe;

import android.os.Parcel;
import android.os.Parcelable;

public final class WaveRecipeOutput implements Parcelable {
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel out, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeOutput> CREATOR = new Parcelable.Creator<WaveRecipeOutput>() {
        public WaveRecipeOutput createFromParcel(Parcel in) {
            return new WaveRecipeOutput(in);
        }
        
        public WaveRecipeOutput[] newArray(int size) {
            return new WaveRecipeOutput[size];
        }
    };
    
    private WaveRecipeOutput(Parcel in) {
        
    }
}