// 
//  WaveSensorDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import android.util.Log;
import java.util.ArrayList;
import org.json.*;

/**
 * WaveSensorDescription
 *
 * This is a representational sensor object, meaning it does (necessarily)
 * correspond to sensors available on the device.  Its denotes requirements
 * of a sensor that can be met, so may be generic, or very precise, depending
 * on the nature of the algorithm.
 */
public class WaveSensorDescription {
    
    private static final String TAG = WaveSensorDescription.class.getSimpleName();
    
    public enum Type { ACCELEROMETER, MAGNETOMETER, LOCATION };
    
    protected Type type;
    
    protected String expectedUnits;
    
    protected ArrayList<WaveSensorChannelDescription> channels;
    
    public static String typeToString(Type t) {
        String s;
        switch (t) {
            case ACCELEROMETER:     s = "ACCELEROMETER";    break;
            case MAGNETOMETER:      s = "MAGNETOMETER";     break;
            case LOCATION:          s = "LOCATION";         break;
            default: throw new AssertionError(t);
        }
        return s;
    }
    
    public WaveSensorDescription(Type t, String expectedUnits) {
        type = t;
        this.expectedUnits = expectedUnits;
        
        channels = new ArrayList<WaveSensorChannelDescription>();
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
    
    // Use @Override to avoid accidental overloading.
    @Override
    public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof WaveSensorDescription)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveSensorDescription lhs = (WaveSensorDescription) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return type == lhs.type &&
            (expectedUnits == null ? lhs.expectedUnits == null
            : expectedUnits.equals(lhs.expectedUnits)) &&
            channels.equals(lhs.channels);
    }
    
    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + type.hashCode();
        
        result = 31 * result +
            (expectedUnits == null ? 0 : expectedUnits.hashCode());
        
        result = 31 * result + channels.hashCode();

        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[" +
            "type=" + type + ", " +
            "expectedUnits=" + expectedUnits + ", " +
            "channels=" + channels + "]";
    }
}