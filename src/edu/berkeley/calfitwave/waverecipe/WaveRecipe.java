// 
//  WaveRecipe.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.calfitwave.waverecipe;

import android.os.Parcel;
import android.os.Parcelable;

public class WaveRecipe implements Parcelable {
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipe> CREATOR = new Parcelable.Creator<WaveRecipe>() {
        public WaveRecipe createFromParcel(Parcel in) {
            return new WaveRecipe(in);
        }
        
        public WaveRecipe[] newArray(int size) {
            return new WaveRecipe[size];
        }
    };
    
    private WaveRecipe(Parcel in) {
        
    }
}