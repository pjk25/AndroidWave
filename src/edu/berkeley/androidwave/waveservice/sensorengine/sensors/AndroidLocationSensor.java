// 
//  AndroidLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import android.content.Context;
import android.content.Intent;
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
 * http://developer.android.com/guide/topics/location/obtaining-user-location.html
 */
public class AndroidLocationSensor extends WaveSensor implements LocationListener {
    
    private static final String TAG = AndroidLocationSensor.class.getSimpleName();
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    // public static final String TEST_PROVIDER_NAME = "edu.berkeley.androidwave.SimpleTestProvider";
    
    public static final float GPS_THRESHOLD = (float)1000.0;
    
    protected Context mContext;
    
    protected LocationManager mLocationManager;
    
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
        
        LocationManager mLocationManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
        
        if (mLocationManager != null) {
            AndroidLocationSensor locationSensor = new AndroidLocationSensor(c, mLocationManager);
            set.add(locationSensor);
        }
        
        return set;
    }
    
    public AndroidLocationSensor(Context c, LocationManager locationManager) {
        super("LOCATION", "degrees");
        
        if (locationManager == null) {
            throw new NullPointerException("locationManager parameter cannot be null");
        }
        
        mContext = c;
        mLocationManager = locationManager;
        started = false;
        currentLocationEstimate = null;
    }
    
    /**
     * returns a specific version string, which should be sufficient to
     * uniquely identify different sensor hardware/plugins
     */
    @Override
    public String getVersion() {
        return VERSION_BASE + "_" + this.getType();
    }

    @Override
    public Double getMaximumAvailableSamplingFrequency() {
        return null;
    }
    
    @Override
    public Double getMaximumAvailablePrecision() {
        // TODO: determine this
        return null;
    }
    
    /**
     * start
     * 
     * @param precisionHint desired precision IN METERS
     */
    @Override
    public void start(WaveSensorListener listener, double rateHint, double precisionHint) throws Exception {
        if (started) {
            throw new Exception("Sensor already started");
        }
        started = true;
        
        this.listener = listener;
        desiredRate = rateHint;

        long minTime = (long) (1000.0 / rateHint);
        float minDistance = (float)precisionHint;
        
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                minTime,
                                                minDistance,
                                                this);

        // TODO: determine the gps use threshold more precisely
        if (precisionHint < GPS_THRESHOLD) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Log.d(TAG, "GPS Provider is not enabled. Enabling GPS Provider...");
                // Intent enableGPS = new Intent("android.location.GPS_ENABLED_CHANGE");
                // enableGPS.putExtra("enabled", true);
                // mContext.sendBroadcast(enableGPS);
                // Log.d(TAG, "GPS Provider has been enabled.");

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                        minTime,
                                                        minDistance,
                                                        this);
            }
        }
        
        // for testing purposes only
        // if (true) { // <- ideally we want to know if this is a test scenario
        //     Log.d(TAG, "AndroidLocationSensor.start: checking for location provider "+TEST_PROVIDER_NAME);
        //     if (mLocationManager.isProviderEnabled(TEST_PROVIDER_NAME)) {
        //         Log.d(TAG, "AndroidLocationSensor.start: requesting location updates from "+TEST_PROVIDER_NAME);
        //         mLocationManager.requestLocationUpdates(TEST_PROVIDER_NAME,
        //                                                 minTime,
        //                                                 minDistance,
        //                                                 this);
        //         // 
        //         // // grab the last location and fake it.
        //         // final Location lastLoc = mLocationManager.getLastKnownLocation(TEST_PROVIDER_NAME);
        //         // if (lastLoc != null) {
        //         //     // fire the event on another thread
        //         //     new Thread(new Runnable() {
        //         //         @Override
        //         //         public void run() {
        //         //             onLocationChanged(lastLoc);
        //         //         }
        //         //     }).start();
        //         // }
        //     }
        // }
    }
    
    @Override
    public void alterRate(double newRate) throws Exception {
        throw new UnsupportedOperationException("alterRate not implemented");
    }
    
    @Override
    public void alterPrecision(double newPrecision) throws Exception {
        throw new UnsupportedOperationException("alterPrecision not implemented");
    }
    
    @Override
    public void stop() throws Exception {
        if (!started) {
            throw new Exception("Sensor has not been started yet");
        }
        
        mLocationManager.removeUpdates(this);
        
        started = false;
    }

    /**
     * ---------------------- LocationListener Methods -----------------------
     */
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged("+location+")");
        
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
        listener.onWaveSensorChanged(new AndroidLocationSensorEvent(this, location.getTime(), values));
        
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