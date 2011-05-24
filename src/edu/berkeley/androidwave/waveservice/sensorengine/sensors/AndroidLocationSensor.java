// 
//  AndroidLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.content.Context;
// import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import java.util.HashSet;
import java.util.Set;

/**
 * AndroidLocationSensor
 * 
 * Specialized {@link WaveSensor} subclass for location.  Should automatically
 * select between Network/GPS/etc.
 */
public class AndroidLocationSensor extends WaveSensor {
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    /**
     * instancesAvailableInContext
     * 
     * Static method, returning a Set of WaveSensor instances representing
     * distinct physical sensors available in the given Context
     * 
     * The default implementation returns an empty set, as the abstract
     * WaveSensor class will never correspond to any sensors (but subclasses
     * may, and should)
     */
    public static Set<WaveSensor> instancesAvailableInContext(Context c) {
        
        Set<WaveSensor> set = new HashSet<WaveSensor>(1);
        
        LocationManager locationManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
        
        if (locationManager != null) {
            AndroidLocationSensor locationSensor = new AndroidLocationSensor("LOCATION", ""); // <- TODO: determine units
            set.add(locationSensor);
        }
        
        return set;
    }
    
    public AndroidLocationSensor(String type, String units) {
        super(type, units);
    }
    
    /**
     * returns a specific version string, which should be sufficient to
     * uniquely identify different sensor hardware/plugins
     */
    public String getVersion() {
        return VERSION_BASE + "_" + this.getType();
    }

    public Double getMaximumAvailableSamplingFrequency() {
        return null;
    }
    
    public Double getMaximumAvailablePrecision() {
        // TODO: determine this
        return null;
    }
    
    public void start(WaveSensorListener listener, double rate) throws Exception {
        
    }
    
    public void alterRate(double newRate) throws Exception {
        
    }
    
    public void stop() throws Exception {
        
    }
    
}