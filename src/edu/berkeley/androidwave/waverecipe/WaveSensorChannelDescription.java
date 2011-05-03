// 
//  WaveSensorChannelDescription.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import org.json.JSONObject;

/**
 * WaveSensorChannelDescription
 *
 * @see WaveSensor
 */

public class WaveSensorChannelDescription {
    
    protected String name;
    
    public WaveSensorChannelDescription(String name) {
        if (name == null) {
            throw new NullPointerException("name parameter cannot be null");
        }
        this.name = name;
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
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
        if (!(o instanceof WaveSensorChannelDescription)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveSensorChannelDescription lhs = (WaveSensorChannelDescription) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return name.equals(lhs.name);
    }
    
    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + name.hashCode();

        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[" +
            "name=" + name + "]";
    }
}