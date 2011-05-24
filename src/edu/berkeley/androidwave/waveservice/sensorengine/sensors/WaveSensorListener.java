// 
//  WaveSensorListener.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.hardware.SensorEvent;

public interface WaveSensorListener {
    
    // TODO: change SensorEvent parameter out for something generic and not tied to android.hardware.Sensor
    public void onWaveSensorChanged(WaveSensor waveSensor, SensorEvent event);
}