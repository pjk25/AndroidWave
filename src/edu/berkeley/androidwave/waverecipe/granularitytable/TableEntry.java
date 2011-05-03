// 
//  TableEntry.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-03.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import java.util.HashSet;
import java.util.Set;

public class TableEntry {
    public Set<SensorAttributes> sensorAttributes;
    public double outputRate;
    public double outputPrecision;
    
    public TableEntry() {
        sensorAttributes = new HashSet<SensorAttributes>();
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
        if (!(o instanceof TableEntry)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        TableEntry lhs = (TableEntry) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return outputRate == lhs.outputRate &&
            outputPrecision == lhs.outputPrecision &&
            sensorAttributes.equals(lhs.sensorAttributes);
    }
    
    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        long doubleFieldBits;
        // Include a hash for each field.
        doubleFieldBits = Double.doubleToLongBits(outputRate);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));

        doubleFieldBits = Double.doubleToLongBits(outputPrecision);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));

        result = 31 * result + sensorAttributes.hashCode();

        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[" +
            "outputRate=" + outputRate + ", " +
            "outputPrecision=" + outputPrecision + ", " +
            "sensorAttributes=" + sensorAttributes + "]";
    }
}
