// 
//  AndroidLocationSensorEvent.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-06-01.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import com.ibm.util.CoordinateConversion;
import com.ibm.util.UtmLocation;
import java.util.Map;

/**
 * Special subclass of WaveSensorEvent for the AndroidLocationSensor which
 * provides special handling of adjusting precisions (at least because values
 * are reported in degrees, but precision is specified in meters)
 */
public class AndroidLocationSensorEvent extends WaveSensorEvent {
    
    CoordinateConversion cc;
    
    public AndroidLocationSensorEvent(AndroidLocationSensor sensor, long timestamp, Map<String, Double> values) {
        super(sensor, timestamp, values);
        
        cc = new CoordinateConversion();
    }
    
    /**
     * getValueConformedToPrecision
     * 
     * rather than quantize, we add noise to the sensed location
     * 
     * TODO: verify the algorithm used below
     */
    @Override
    public double getValueConformedToPrecision(String name, double step) {
        if (name.equals("accuracy")) {
            throw new UnsupportedOperationException("accuracy cannot be conformed");
        }
        if (name.equals("bearing")) {
            throw new UnsupportedOperationException("bearing cannot be conformed");
        }
        if (name.equals("speed")) {
            throw new UnsupportedOperationException("speed cannot be conformed");
        }
        
        boolean shouldFilter = true;
        if (values.containsKey("accuracy")) {
            if (values.get("accuracy").doubleValue() >= step) {
                // reading is too accurate
                shouldFilter = false;
            }
        }
        
        double theValue = values.get(name);
        
        if (shouldFilter) {
            if (name.equals("latitude") || name.equals("longitude")) {
                
                double lat = values.get("latitude").doubleValue();
                double lon = values.get("longitude").doubleValue();
                
                UtmLocation utm = cc.latLon2UtmLocation(lat, lon);
                
                double angle = 2 * Math.PI * Math.random();
                double dist = Math.random() * step / 2.0;
                
                double eShift = dist * Math.cos(angle);
                double nShift = dist * Math.sin(angle);
                
                utm.shiftEasting(eShift);
                utm.shiftNorthing(nShift);
                
                double[] nLatLon = cc.utm2LatLon(utm.toString());
                
                if (name.equals("latitude")) {
                    return nLatLon[0];
                } else {
                    return nLatLon[1];
                }
            } else if (name.equals("altitude")) {
                // we have already introduced a change in location that may be
                // as much as step, so we only add up to 1/4 step to the
                // altitude
                theValue += (Math.random() - 0.5) * step / 4.0;
            }
        }
        
        return theValue;
    }
}