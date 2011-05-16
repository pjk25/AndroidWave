// 
//  SensorAttributes.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-03.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

/**
 * SensorAttributes
 * 
 * A simple storage object that relates a SensorDescription, rate, and
 * precision for the purpose of a WaveRecipeAuthorization
 * 
 * @see WaveRecipeAuthorization
 */
public class SensorAttributes {
    public WaveSensorDescription sensorDescription;
    public double rate;
    public double precision;
    
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
        if (!(o instanceof SensorAttributes)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        SensorAttributes lhs = (SensorAttributes) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return rate == lhs.rate &&
            precision == lhs.precision &&
            (sensorDescription == null ? lhs.sensorDescription == null
            : sensorDescription.equals(lhs.sensorDescription));
    }
    
    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        long doubleFieldBits;
        // Include a hash for each field.
        doubleFieldBits = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));

        doubleFieldBits = Double.doubleToLongBits(precision);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));

        result = 31 * result +
                 (sensorDescription == null ? 0 : sensorDescription.hashCode());

        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[" +
            "rate=" + rate + ", " +
            "precision=" + precision + ", " +
            "sensorDescription=" + sensorDescription + "]";
    }
}