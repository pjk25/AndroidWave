// 
//  WaveRecipeOutputDataImpl.java
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
 * WaveRecipeOutputDataImpl
 *
 * Meant to encapsulate the computed output from a recipe, so that it can be
 * sent out to the Wave Client app authorized for that recipe.
 * 
 * TODO: write the test for this class
 */
public final class WaveRecipeOutputDataImpl implements Parcelable {
    
    private static final String TAG = WaveRecipeOutputDataImpl.class.getSimpleName();
    
    protected long time;
    
    protected Bundle values;
    
    public WaveRecipeOutputDataImpl(long time, Map<String, Double> values) {
        if (values == null) {
            throw new NullPointerException("values parameter cannot be null");
        }
        
        Log.d(TAG, "WaveRecipeOutputDataImpl.<init>: values => "+values);
        
        this.time = time;
        
        this.values = new Bundle(values.size());
        for (String key : values.keySet()) {
            Double thisValue = values.get(key);
            Log.d(TAG, "My Double.class.hashCode() => "+Double.class.hashCode());
            Log.d(TAG, "thisValue => "+thisValue);
            Log.d(TAG, "thisValue.getClass().hashCode() => "+thisValue.getClass().hashCode());
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
        return getClass().getName() + "[" +
            "time=" + time + ", " +
            "values=" + values + "]";
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
    
    public static final Parcelable.Creator<WaveRecipeOutputDataImpl> CREATOR = new Parcelable.Creator<WaveRecipeOutputDataImpl>() {
        public WaveRecipeOutputDataImpl createFromParcel(Parcel in) {
            return new WaveRecipeOutputDataImpl(in);
        }
        
        public WaveRecipeOutputDataImpl[] newArray(int size) {
            return new WaveRecipeOutputDataImpl[size];
        }
    };
    
    private WaveRecipeOutputDataImpl(Parcel in) {
        time = in.readLong();
        values = in.readBundle();
    }
}