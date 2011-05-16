// 
//  ParcelableWaveRecipeOutputData.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.Map;

/**
 * ParcelableWaveRecipeOutputData
 *
 * Meant to encapsulate the computed output from a recipe, so that it can be
 * sent out to the Wave Client app authorized for that recipe.
 * 
 * TODO: write the test for this class
 */
public final class ParcelableWaveRecipeOutputData implements Parcelable {
    
    private static final String TAG = ParcelableWaveRecipeOutputData.class.getSimpleName();
    
    protected long time;
    
    protected Bundle values;
    
    public ParcelableWaveRecipeOutputData(long time, Map<String, Double> values) {
        if (values == null) {
            throw new NullPointerException("values parameter cannot be null");
        }
        
        // Log.d(TAG, "ParcelableWaveRecipeOutputData.<init>: values => "+values);
        
        this.time = time;
        
        this.values = new Bundle(values.size());
        for (String key : values.keySet()) {
            Double thisValue = values.get(key);
            // Log.d(TAG, "My Double.class.hashCode() => "+Double.class.hashCode());
            // Log.d(TAG, "thisValue => "+thisValue);
            // Log.d(TAG, "thisValue.getClass().hashCode() => "+thisValue.getClass().hashCode());
            this.values.putDouble(key, thisValue.doubleValue());
        }
    }
    
    public long getTime() {
        return time;
    }
    
    public boolean hasChannelName(String name) {
        return values.containsKey(name);
    }
    
    public double getChannelValue(String name) throws Exception {
        return ((Number)values.get(name)).doubleValue();
    }
    
    public String toString() {
        String s = getClass().getName() + "[" +
                    "time=" + time + ", ";
        
        s += "values={";
        boolean first = true;
        for (String key : values.keySet()) {
            if (!first) s += ",";
            s += key + "=" + values.get(key);
            first = false;
        }
        s += "}";
        
        s += "]";
        
        return s;
    }
    
    /**
     * Parcelable Methods
     */
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(time);
        out.writeBundle(values);
    }
    
    public static final Parcelable.Creator<ParcelableWaveRecipeOutputData> CREATOR = new Parcelable.Creator<ParcelableWaveRecipeOutputData>() {
        public ParcelableWaveRecipeOutputData createFromParcel(Parcel in) {
            return new ParcelableWaveRecipeOutputData(in);
        }
        
        public ParcelableWaveRecipeOutputData[] newArray(int size) {
            return new ParcelableWaveRecipeOutputData[size];
        }
    };
    
    private ParcelableWaveRecipeOutputData(Parcel in) {
        time = in.readLong();
        values = in.readBundle();
    }
}