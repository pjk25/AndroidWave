// 
//  WaveLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.content.Context;

/**
 * LocationWaveSensor
 * 
 * Specialized {@link WaveSensor} subclass for location.  Should automatically
 * select between Network/GPS/etc.
 */
public class LocationWaveSensor extends WaveSensor {
    
    public LocationWaveSensor(Context c) {
        super(WaveSensorDescription.Type.LOCATION, "");    // TODO: determine units
    }
}