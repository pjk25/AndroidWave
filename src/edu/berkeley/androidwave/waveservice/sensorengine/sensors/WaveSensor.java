// 
//  WaveSensor.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-07.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine.sensors;

import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;
import edu.berkeley.androidwave.waverecipe.WaveSensorChannelDescription;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generic Sensor class for AndroidWave
 * 
 * TODO: use a plug-in architecture for WaveSensor subclasses, as we will need
 *       extras for different locally available hardware (when we support)
 *       external sensors.
 */
public abstract class WaveSensor {
    
    protected String type;
    protected String units;
    
    protected ArrayList<WaveSensorChannel> channels;
    
    // a bit of local storage for the SensorEngine
    public double desiredRate;

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
        return new HashSet<WaveSensor>();
    }
    
    /**
     * Constructor
     */
    public WaveSensor(String type, String units) {
        if (type == null) {
            throw new NullPointerException("type parameter cannot be null");
        }
        if (units == null) {
            throw new NullPointerException("units parameter cannot be null");
        }
        
        this.type = type;
        this.units = units;
        
        channels = new ArrayList<WaveSensorChannel>();
    }
    
    /**
     * type getter
     */
    public String getType() {
        return type;
    }
    
    /**
     * units getter
     */
    public String getUnits() {
        return units;
    }
    
    /**
     * channels getter
     */
    public List<WaveSensorChannel> getChannels() {
        return channels;
    }
    
    /**
     * returns a specific version string, which should be sufficient to
     * uniquely identify different sensor hardware/plugins
     */
    public abstract String getVersion();
    
    /**
     * returns null if unknown
     */
    public abstract Double getMaximumAvailableSamplingFrequency();
    
    /**
     * returns null if unknown
     */
    public abstract Double getMaximumAvailablePrecision();
    
    /**
     * We throw an exception if the sensor is already started, as by
     * convention, we fan out the sensor data from a single WaveSensorListener
     * outside of this instance. Currently, this will be the SensorEngine.
     */
    public abstract void start(WaveSensorListener listener, double rate) throws Exception;
    
    /**
     * throws an exception if the sensor has not been started, or if the
     * requested newRate is faster than the maximum rate for this sensor (if
     * the maximum rate is not null).
     */
    public abstract void alterRate(double newRate) throws Exception;
    
    /**
     * stop the sensor
     */
    public abstract void stop() throws Exception;
    
    /**
     * return the names of this sensor's channels as an ArrayList
     */
    private ArrayList<String> getChannelNamesArrayList() {
        ArrayList<String> channelNames = new ArrayList<String>(channels.size());
        for (WaveSensorChannel wsc : channels) {
            channelNames.add(wsc.getName());
        }
        return channelNames;
    }
    
    /**
     * indicates if this wavesensor is in fact a "match" for a
     * {@code WaveSensorDescription} object used in a WaveRecipe
     */
    public boolean matchesWaveSensorDescription(WaveSensorDescription wsd)
            throws NullPointerException {
        
        if (wsd == null) throw new NullPointerException("WaveSensor matching to a null description is undefined");
        
        boolean doesMatch = true;
        
        doesMatch &= this.getType().equals(WaveSensorDescription.typeToString(wsd.getType()));
        if (wsd.hasExpectedUnits()) {
            doesMatch &= (this.getUnits().equals(wsd.getExpectedUnits()));
        }
        
        if (wsd.hasChannels()) {
            // construct an ArrayList of the wsd's channels
            WaveSensorChannelDescription[] wsdChannels = wsd.getChannels();
            ArrayList<String> wsdChannelNames = new ArrayList<String>(wsdChannels.length);
            for (int i=0; i<wsdChannels.length; i++) {
                wsdChannelNames.add(wsdChannels[i].getName());
            }
            
            // available channel names should be a superset of the description
            // channel names
            doesMatch &= (this.getChannelNamesArrayList().containsAll(wsdChannelNames));
        }
        return doesMatch;
    }
}