// 
//  WaveRecipeOutputChannelDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-23.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class WaveRecipeOutputChannelDescription implements Parcelable {
    
    private static final String TAG = "WaveRecipeOutputChannelDescription";
    
    protected String name;
    
    public WaveRecipeOutputChannelDescription(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    // Use @Override to avoid accidental overloading.
    @Override public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof WaveRecipeOutputChannelDescription)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveRecipeOutputChannelDescription lhs = (WaveRecipeOutputChannelDescription) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return (name == null ? lhs.name == null : name.equals(lhs.name));
    }
    
    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + (name == null ? 0 : name.hashCode());

        return result;
    }
    
    @Override public String toString() {
        return getClass().getName() + "[" +
            "name=" + name + "]";
    }

    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel("+dest+", "+flags+")");
        dest.writeString(name);
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
        Log.d(TAG, "WaveRecipeOutputChannelDescription(Parcel "+in+")");
        name = in.readString();
    }
}