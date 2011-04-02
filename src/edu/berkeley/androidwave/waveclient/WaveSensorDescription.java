// 
//  WaveSensorDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Vector;

/**
 * WaveSensorDescription
 *
 * This is a representational sensor object, meaning it does (necessarily)
 * correspond to sensors available on the device.  Its denotes requirements
 * of a sensor that can be met, so may be generic, or very precise, depending
 * on the nature of the algorithm.
 */
public class WaveSensorDescription implements Parcelable {
    
    public enum Type { ACCELEROMETER, MAGNETOMETER, LOCATION };
    
    protected Type type;
    
    protected String expectedUnits;
    
    protected Vector<WaveSensorChannelDescription> channels;
    
    public WaveSensorDescription(Type t, String expectedUnits) {
        type = t;
        this.expectedUnits = expectedUnits;
        
        channels = new Vector<WaveSensorChannelDescription>();
    }
    
    /**
     * getType
     */
    public Type getType() {
        return type;
    }
    
    /**
     * hasExpectedUnits
     */
    public boolean hasExpectedUnits() {
        return (expectedUnits != null);
    }
    
    /**
     * getExpectedUnits
     */
    public String getExpectedUnits() {
        return expectedUnits;
    }
    
    /**
     * hasChannels
     */
    public boolean hasChannels() {
        return (channels.size() > 0);
    }
    
    /**
     * getChannels
     */
    public WaveSensorChannelDescription[] getChannels() {
        return channels.toArray(new WaveSensorChannelDescription[0]);
    }
    
    /**
     * addChannel
     */
    public boolean addChannel(WaveSensorChannelDescription c) {
        return channels.add(c);
    }

    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveSensorDescription> CREATOR = new Parcelable.Creator<WaveSensorDescription>() {
        public WaveSensorDescription createFromParcel(Parcel in) {
            return new WaveSensorDescription(in);
        }
        
        public WaveSensorDescription[] newArray(int size) {
            return new WaveSensorDescription[size];
        }
    };
    
    private WaveSensorDescription(Parcel in) {
        
    }
}