// 
//  WaveRecipeOutputChannelDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Parcel;
import android.os.Parcelable;

public class WaveRecipeOutputChannelDescription implements Parcelable {
    
    protected String name;
    
    public WaveRecipeOutputChannelDescription(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipeOutputChannelDescription> CREATOR = new Parcelable.Creator<WaveRecipeOutputChannelDescription>() {
        public WaveRecipeOutputChannelDescription createFromParcel(Parcel in) {
            return new WaveRecipeOutputChannelDescription(in);
        }
        
        public WaveRecipeOutputChannelDescription[] newArray(int size) {
            return new WaveRecipeOutputChannelDescription[size];
        }
    };
    
    private WaveRecipeOutputChannelDescription(Parcel in) {
        
    }
}