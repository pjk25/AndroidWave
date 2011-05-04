// 
//  WaveRecipeOutputDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * WaveRecipeOutputDescription
 * 
 * General representation of one output from a WaveRecipe
 */
public class WaveRecipeOutputDescription implements Parcelable {
    
    public static final String TAG = "WaveRecipeOutputDescription";
    
    protected String name;
    protected String units;
    
    protected ArrayList<WaveRecipeOutputChannelDescription> channels;
    
    public WaveRecipeOutputDescription(String name, String units) {
        this.name = name;
        this.units = units;
        
        channels = new ArrayList<WaveRecipeOutputChannelDescription>();
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
    }
    
    /**
     * getUnits
     */
    public String getUnits() {
        return units;
    }
    
    /**
     * getChannels
     */
    public WaveRecipeOutputChannelDescription[] getChannels() {
        return channels.toArray(new WaveRecipeOutputChannelDescription[0]);
    }

    /**
     * addChannel
     */
    public boolean addChannel(WaveRecipeOutputChannelDescription c) {
        return channels.add(c);
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
        if (!(o instanceof WaveRecipeOutputDescription)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveRecipeOutputDescription lhs = (WaveRecipeOutputDescription) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return (name == null ? lhs.name == null : name.equals(lhs.name))
               && (units == null ? lhs.units == null : units.equals(lhs.units))
               && channels.equals(lhs.channels); // channels should never be null
    }
    
    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (units == null ? 0 : units.hashCode());
        result = 31 * result + channels.hashCode();

        return result;
    }
    
    @Override public String toString() {
        return getClass().getName() + "[" +
            "name=" + name + ", " +
            "units=" + units + ", " +
            "channels=" + channels + "]";
    }

    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(units);
        Log.d(TAG, "writeToParcel => "+channels);
        dest.writeTypedList(channels);
    }
    
    public static final Parcelable.Creator<WaveRecipeOutputDescription> CREATOR = new Parcelable.Creator<WaveRecipeOutputDescription>() {
        public WaveRecipeOutputDescription createFromParcel(Parcel in) {
            return new WaveRecipeOutputDescription(in);
        }
        
        public WaveRecipeOutputDescription[] newArray(int size) {
            return new WaveRecipeOutputDescription[size];
        }
    };
    
    private WaveRecipeOutputDescription(Parcel in) {
        name = in.readString();
        units = in.readString();

        channels = new ArrayList<WaveRecipeOutputChannelDescription>();
        Log.d(TAG, "WaveRecipeOutputChannelDescription(Parcel in) => "+channels);
        in.readTypedList(channels, WaveRecipeOutputChannelDescription.CREATOR);
        Log.d(TAG, "WaveRecipeOutputChannelDescription(Parcel in) => "+channels);
    }
}