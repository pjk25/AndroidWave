// 
//  WaveLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import android.content.Context;

/**
 * LocationWaveSensor
 * 
 * Specialized {@link WaveSensor} subclass for location.  Should automatically
 * select between Network/GPS/etc.
 */
public class LocationWaveSensor extends WaveSensor {
    
    public LocationWaveSensor(Context c) {
        super(WaveSensor.Type.LOCATION);
    }
}