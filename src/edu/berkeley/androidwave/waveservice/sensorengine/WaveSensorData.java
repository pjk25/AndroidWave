// 
//  WaveSensorData.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-02.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.os.Parcel;
import android.os.Parcelable;

public class WaveSensorData {
    
    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveSensorData> CREATOR = new Parcelable.Creator<WaveSensorData>() {
        public WaveSensorData createFromParcel(Parcel in) {
            return new WaveSensorData(in);
        }
        
        public WaveSensorData[] newArray(int size) {
            return new WaveSensorData[size];
        }
    };
    
    private WaveSensorData(Parcel in) {
        
    }
    
}