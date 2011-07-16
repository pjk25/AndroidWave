// 
//  AndroidLocationSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.ibm.util.CoordinateConversion;
import com.ibm.util.UtmLocation;
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
public class AndroidLocationSensor extends WaveSensor {
    
    private static final String TAG = AndroidLocationSensor.class.getSimpleName();
    
    protected final String VERSION_BASE = Build.DEVICE + "_" + Build.BOARD + "_" + Build.MODEL;
    
    private static final String[] CHANNEL_NAMES = new String[] {"latitude", "longitude", "altitude"};
    
    public static final float GPS_THRESHOLD = (float)1000.0;
    
    protected static CoordinateConversion cc = new CoordinateConversion();
    
    protected LocationManager mLocationManager;
    
    protected Location currentLocationEstimate;
    
    Map<WaveRecipeAlgorithm, LocationDataForwarder> forwarderMap;
    
    class LocationDataForwarder implements LocationListener {
        long lastForwardTime;
        int sensorManagerRate;
        long minOutputInterval;
        double maxOutputPrecision;  // in meters
        WaveSensorDescription authorizedDescription;
        WaveRecipeAlgorithm destination;
        
        Looper looper;
        
        LocationDataForwarder(double rate, double precision, WaveSensorDescription wsd, WaveRecipeAlgorithm dest, Looper l) {
            lastForwardTime = 0;
            minOutputInterval = (long) (1000.0 / rate); // LocationSensor uses millisecond timestamps
            maxOutputPrecision = precision;
            authorizedDescription = wsd;
            destination = dest;
            looper = l;
        }

        /**
         * ---------------------- LocationListener Methods -----------------------
         */
        public void onLocationChanged(Location location) {
            // Log.v(TAG, "onLocationChanged("+location+")");
            
            // TODO: optimize this method if necessary

            if (isBetterLocation(location, currentLocationEstimate)) {
                currentLocationEstimate = location;
            }
            
            long interval = location.getTime() - lastForwardTime;
            
            if (interval >= 0.9 * minOutputInterval) {
                lastForwardTime = location.getTime();
                
                Map<String, Double> values = new HashMap<String, Double>(6);
                values.put("latitude", new Double(location.getLatitude()));
                values.put("longitude", new Double(location.getLongitude()));
                if (location.hasAltitude()) {
                    values.put("altitude", new Double(location.getAltitude()));
                }
                // Bearing and Speed omitted so better lat/lon cannot be
                // inferred after they are artificially degraded
                // if (location.hasBearing()) {
                //     values.put("bearing", new Double(location.getBearing()));
                // }
                // if (location.hasSpeed()) {
                //     values.put("speed", new Double(location.getSpeed()));
                // }
                
                // degrade location values if necessary
                boolean shouldFilter = (!location.hasAccuracy()) || location.getAccuracy() < maxOutputPrecision;
                if (shouldFilter) {
                    double lat = values.get("latitude").doubleValue();
                    double lon = values.get("longitude").doubleValue();
                    
                    UtmLocation utm = cc.latLon2UtmLocation(lat, lon);
                    
                    double angle = 2 * Math.PI * Math.random();
                    // TODO: should dist be the difference between the max precision and the given accuracy?
                    double dist = Math.random() * maxOutputPrecision / 2.0;
                    
                    double eShift = dist * Math.cos(angle);
                    double nShift = dist * Math.sin(angle);
                    
                    utm.shiftEasting(eShift);
                    utm.shiftNorthing(nShift);
                    
                    double[] nLatLon = cc.utm2LatLon(utm.toString());
                    
                    values.put("latitude", new Double(nLatLon[0]));
                    values.put("longitude", new Double(nLatLon[1]));
                    
                    if (location.hasAltitude()) {
                        double newAlt = location.getAltitude() + ((Math.random() - 0.5) * maxOutputPrecision / 4.0);
                        values.put("altitude", new Double(newAlt));
                    }
                }
                
                // for now simple channel handling
                // TODO: better channel handling
                // WaveSensorChannelDescription[] wscds = authorizedDescription.getChannels();
                // if (wscds.length > 0) {
                //     values = new HashMap<String, Double>();
                //     for (WaveSensorChannelDescription wscd : wscds) {
                //         String name = wscd.getName();
                //         values.put(name, sensorEventQuantizedChannel(event, name, maxOutputPrecision));
                //     }
                // } else {
                //     // no channels specified, send all
                //     values = sensorEventQuantized(event, maxOutputPrecision);
                // }
                // assert values.size() > 0;
                
                // call up the algorithmInstance of the authorization
                // TODO: call ingestSensorData on different thread
                try {
                    destination.ingestSensorData(location.getTime(), values);
                } catch (Exception e) {
                    Log.d(TAG, "onLocationChanged", e);
                }
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
    }
    
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
        
        if (mContext == null) mContext = c;

        Set<WaveSensor> set = new HashSet<WaveSensor>(1);
        
        LocationManager mLocationManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
        
        if (mLocationManager != null) {
            AndroidLocationSensor locationSensor = new AndroidLocationSensor(c, mLocationManager);
            
            locationSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[0]));
            locationSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[1]));
            locationSensor.channels.add(new WaveSensorChannel(CHANNEL_NAMES[2]));
            
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
        forwarderMap = new HashMap<WaveRecipeAlgorithm, LocationDataForwarder>();
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
     * 
     */
    @Override
    public synchronized void registerListener(WaveRecipeAlgorithm listener, WaveSensorDescription wsd, double rateHint, double precisionHint)
            throws Exception {
        // Log.v(TAG, String.format("registerListener(%s, %s, %f, %f)", listener, wsd, rateHint, precisionHint));

        if (forwarderMap.containsKey(listener)) {
            throw new Exception(""+listener+" already registered.");
        }
        
        incrementActiveCount();
        
        // need to start a thread here that can receive the location updates
        
        final boolean useGps = precisionHint < GPS_THRESHOLD;
        final long minTime = (long) (1000.0 / rateHint);  // in milliseconds
        final float minDistance = (float)precisionHint;
        
        final double finalRateHint = rateHint;
        final double finalPrecisionHint = precisionHint;
        
        final WaveSensorDescription finalWsd = wsd;
        final WaveRecipeAlgorithm finalListener = listener;
        
        new Thread(new Runnable() {
            public void run() {
                
                Looper.prepare();
                
                // Log.v(TAG, "Looper.myLooper() => "+Looper.myLooper());
                
                LocationDataForwarder ldf = new LocationDataForwarder(finalRateHint,
                                                                      finalPrecisionHint,
                                                                      finalWsd,
                                                                      finalListener,
                                                                      Looper.myLooper());
                
                // TODO: forwarderMap should be a synchronized collection?
                forwarderMap.put(finalListener, ldf);
                
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                        minTime,
                                                        minDistance,
                                                        ldf);

                // TODO: determine the gps use threshold more precisely
                if (useGps) {
                    if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // Log.d(TAG, "GPS Provider is not enabled. Enabling GPS Provider...");
                        // Intent enableGPS = new Intent("android.location.GPS_ENABLED_CHANGE");
                        // enableGPS.putExtra("enabled", true);
                        // mContext.sendBroadcast(enableGPS);
                        // Log.d(TAG, "GPS Provider has been enabled.");

                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                                minTime,
                                                                minDistance,
                                                                ldf);
                    }
                }

                // Looper.loop() is required to keep this thread alive so that location
                // updates are actually delivered
                Looper.loop();
                Log.d(TAG, "end of run() reached.");
            }
        }).start();
    }
    
    /**
     * 
     */
    @Override
    public synchronized void unregisterListener(WaveRecipeAlgorithm listener)
            throws Exception {
        if (!forwarderMap.containsKey(listener)) {
            throw new Exception(""+listener+" not yet registered.");
        }
        
        LocationDataForwarder ldf = forwarderMap.get(listener);
        mLocationManager.removeUpdates(ldf);
        ldf.looper.quit();
        forwarderMap.remove(listener);
        
        decrementActiveCount();
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