// 
//  AndroidLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AndroidLocationSensor
 * 
 * Specialized {@link WaveSensor} subclass for location.  Should automatically
 * select between Network/GPS/etc.
 * 
 * TODO: add some kind of accurracy hint, possibly to WaveSensor
 * 
 * http://developer.android.com/guide/topics/location/obtaining-user-location.html
 */
public class AndroidLocationSensor extends WaveSensor implements LocationListener {
    
    private static final String TAG = AndroidLocationSensor.class.getSimpleName();
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    protected LocationManager locationManager;
    
    protected Location currentLocationEstimate;
    
    protected WaveSensorListener listener;
    
    protected boolean started;
    protected int sensorManagerRate;
    protected long lastSampleTime;
    protected double estimatedRate;

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
            AndroidLocationSensor locationSensor = new AndroidLocationSensor(locationManager);
            set.add(locationSensor);
        }
        
        return set;
    }
    
    public AndroidLocationSensor(LocationManager locationManager) {
        super("LOCATION", "degrees");
        
        if (locationManager == null) {
            throw new NullPointerException("locationManager parameter cannot be null");
        }
        
        this.locationManager = locationManager;
        currentLocationEstimate = null;
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
    
    /**
     * TODO: add an accuracy hint that will allow us to decide whether or not
     *       to use GPS (power intensive)
     */
    public void start(WaveSensorListener listener, double rate) throws Exception {
        long minTime = (long) (1000.0 / rate);
        float minDistance = 0;
        
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                               minTime,
                                               minDistance,
                                               this);
    }
    
    public void alterRate(double newRate) throws Exception {
        throw new Exception("not implemented yet");
    }
    
    public void stop() throws Exception {
        locationManager.removeUpdates(this);
    }

    /**
     * ---------------------- LocationListener Methods -----------------------
     */
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, currentLocationEstimate)) {
            currentLocationEstimate = location;
        }
        
        // first update sensor stats for this sensor
        long last = lastSampleTime;
        lastSampleTime = location.getTime();
        estimatedRate = 1000.0 / (location.getTime() - last);
        
        Map<String, Double> values = new HashMap<String, Double>(6);
        values.put("latitude", new Double(location.getLatitude()));
        values.put("longitude", new Double(location.getLongitude()));
        if (location.hasAccuracy()) {
            values.put("accuracy", new Double(location.getAccuracy()));
        }
        if (location.hasAltitude()) {
            values.put("altitude", new Double(location.getAltitude()));
        }
        if (location.hasBearing()) {
            values.put("bearing", new Double(location.getBearing()));
        }
        if (location.hasSpeed()) {
            values.put("speed", new Double(location.getSpeed()));
        }
        
        
        // dispatch the data to the listener
        listener.onWaveSensorChanged(new WaveSensorEvent(this, location.getTime(), values));
        
        // adjust the rate if necessary
        if (estimatedRate < 0.9 * desiredRate) {
            // TODO: increase sensor rate
            Log.w(TAG, ""+this+" estimated rate less than 90% of desired rate ("+estimatedRate+" < 0.9 * "+desiredRate+")");
        }
    }
    
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged("+provider+", "+status+", "+extras+")");
    }
    
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled("+provider+")");
    }
    
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled("+provider+")");
    }
    
    /**
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     * 
     * Accessed on 2011-5-27
     */
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
            currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}