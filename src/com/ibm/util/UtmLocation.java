// 
//  UtmLocation.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-06-01.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package com.ibm.util;

import android.util.Log;

public class UtmLocation {
    
    private static final String TAG = UtmLocation.class.getSimpleName();
    
    protected String longZone;
    protected String latZone;
    
    protected double easting;
    protected double northing;
    
    public UtmLocation(String longZone, String latZone, double easting, double northing) {
        this.longZone = longZone;
        this.latZone = latZone;
        
        this.northing = northing;
        this.easting = easting;
    }
    
    public void shiftEasting(double shift) {
        // TODO: also check upper bound
        if (easting + shift >= 0.0) {
            easting += shift;
        } else {
            Log.w(TAG, "shiftEasting ignored");
        }
    }
    
    public void shiftNorthing(double shift) {
        // TODO: also check upper bound
        if (northing + shift >= 0.0) {
            northing += shift;
        } else {
            Log.w(TAG, "shiftNorthing ignored");
        }
    }
    
    public String toString() {
        return longZone + " " + latZone + " " + ((int) easting) + " "
              + ((int) northing);
    }
}