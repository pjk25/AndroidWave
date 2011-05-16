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
import java.util.HashMap;
import java.util.Map;

/**
 * ParcelableWaveRecipeOutputData
 *
 * Meant to encapsulate the computed output from a recipe, so that it can be
 * sent out to the Wave Client app authorized for that recipe.
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
    
    protected Map<String, Object> valuesAsMap() {
        Map<String, Object> map = new HashMap<String, Object>(values.size());
        for (String key : values.keySet()) {
            map.put(key, values.get(key));
        }
        return map;
    }
    
    /**
     * Currently android.os.Bundle does not implement equals, so we compare
     * it by converting it to a Map
     */
    @Override public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof ParcelableWaveRecipeOutputData)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        ParcelableWaveRecipeOutputData lhs = (ParcelableWaveRecipeOutputData) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return time == lhs.time &&
            valuesAsMap().equals(lhs.valuesAsMap());
    }
    
    /**
     * Currently android.os.Bundle does not implement equals, and so we use
     * the Map conversion
     */
    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + (int) (time ^ (time >>> 32));

        result = 31 * result + valuesAsMap().hashCode();

        return result;
    }
    
    public String toString() {
        return getClass().getName() + "[" +
                    "time=" + time + ", " +
                    "values=" + valuesAsMap() + "]";
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