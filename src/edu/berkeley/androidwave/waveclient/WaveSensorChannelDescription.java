// 
//  WaveSensorChannelDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WaveSensorChannelDescription
 *
 * @see WaveSensor
 */

public class WaveSensorChannelDescription implements Parcelable {
    
    protected String name;
    
    public WaveSensorChannelDescription(String name) {
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
    
    public static final Parcelable.Creator<WaveSensorChannelDescription> CREATOR = new Parcelable.Creator<WaveSensorChannelDescription>() {
        public WaveSensorChannelDescription createFromParcel(Parcel in) {
            return new WaveSensorChannelDescription(in);
        }
        
        public WaveSensorChannelDescription[] newArray(int size) {
            return new WaveSensorChannelDescription[size];
        }
    };
    
    private WaveSensorChannelDescription(Parcel in) {
        
    }
}