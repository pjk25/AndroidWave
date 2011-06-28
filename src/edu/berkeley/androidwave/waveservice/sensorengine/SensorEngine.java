// 
//  SensorEngine.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
//  
//  Uses portions by Mr. Charles Wang <charleswang007@gmail.com>
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waveexception.SensorNotAvailableException;
import edu.berkeley.androidwave.waverecipe.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.*;
import edu.berkeley.androidwave.waveservice.sensorengine.sensors.*;

import android.content.Context;
// import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SensorEngine
 * 
 * SensorEngine is a singleton, because there is only one set of underlying
 * hardware sensors.
 */
public class SensorEngine {
    
    private static final String TAG = SensorEngine.class.getSimpleName();
    
    protected static SensorEngine theInstance;
    
    
    class AlgorithmOutputForwarder implements WaveRecipeAlgorithmListener {
        long lastForwardTime;
        long minOutputInterval;
        double maxOutputPrecision;
        WaveRecipeAuthorization authorization;
        WaveRecipeOutputListener destination;
        
        AlgorithmOutputForwarder(double rate, double precision, WaveRecipeAuthorization auth, WaveRecipeOutputListener dest) {
            lastForwardTime = 0;
            minOutputInterval = (long) (1000.0 / rate); // in milliseconds
            maxOutputPrecision = precision;
            authorization = auth;
            destination = dest;
        }
        
        /**
         * NOTE: the quantization step here might cause problems with location
         *       data, as precision is set in meters
         */
        public void handleRecipeData(long time, Map<String, Double> values) {
            // Log.v(TAG+"/AlgorithmOutputForwarder", "handleRecipeData("+data+")");
            try {
                // TODO: should we use the data's timestamp instead of SystemClock.elapsedRealtime() ?
                // drop this data if it exceeds the max rate
                long now = SystemClock.elapsedRealtime();
                long thisInterval = now - lastForwardTime;
                if (thisInterval >= 0.9 * minOutputInterval) {
                    // rate is good, truncate precision
                    // quantizeValueMap(values, maxOutputPrecision);
                    destination.receiveDataForAuthorization(time, values, authorization);
                    lastForwardTime = now;
                } else {
                    Log.d(TAG, String.format("Dropped excessive recipe output (thisInterval => %d, minOutputInterval => %d)", thisInterval, minOutputInterval));
                    Log.v(TAG, String.format("\ttime => %d, now => %d, (delta => %d)", time/(1000*1000), now, (now - time/(1000*1000))));
                }
            } catch (Exception e) {
                Log.d("AlgorithmOutputForwarder", "Exception encountered", e);
            }
        }
        
        private void quantizeValueMap(Map<String, Double> m, double s) {
            for (Map.Entry<String, Double> entry : m.entrySet()) {
                double v = entry.getValue().doubleValue();
                
                long factor = (long) (v / s);
                v = ((double)factor) * s;
                
                m.put(entry.getKey(), v);
            }
        }
    }
    
    
    /**
     * non-static fields
     */
    protected Context mContext;
    protected Set<WaveSensor> availableLocalSensors;
    protected Map<WaveRecipeAuthorization, WaveRecipeLocalDeviceSupportInfo> scheduledAuthorizations;
    protected Map<WaveRecipeAuthorization, WaveRecipeAlgorithm> authorizationToAlgorithmMap;
    
    /**
     * Private Constructor for Singleton
     */
    private SensorEngine(Context c) {
        mContext = c;
        
        availableLocalSensors = null;
        scheduledAuthorizations = new HashMap<WaveRecipeAuthorization, WaveRecipeLocalDeviceSupportInfo>();
        authorizationToAlgorithmMap = new HashMap<WaveRecipeAuthorization, WaveRecipeAlgorithm>();
    }
    
    public static void init(Context c) { //throws Exception {
        if (theInstance != null) {
            Log.w(TAG, "SensorEngine.init() called more than once, dropping singleton instance "+theInstance);
            //throw new Exception("SensorEngine.init can only be called once");
        }
        theInstance = new SensorEngine(c);
    }
    
    /**
     * getInstance
     * 
     * Access the Singleton SensorEngine instance (without needing to supply
     * a context object)
     */
    public static SensorEngine getInstance() throws Exception {
        if (theInstance == null) {
            throw new Exception("SensorEngine.init not yet called.");
        }
        return theInstance;
    }
    
    protected String internalIdForSensor(WaveSensor s) {
        return s.getVersion();
    }
    
    protected WaveSensor sensorForInternalId(String internalId) {
        if (internalId == null) throw new NullPointerException("internalId parameter cannot be null");
        
        Set<WaveSensor> sensors = getAvailableLocalSensors();
        for (WaveSensor s : sensors) {
            if (s.getVersion().equals(internalId)) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * getAvailableLocalSensors
     */
    protected Set<WaveSensor> getAvailableLocalSensors() {
        if (availableLocalSensors == null) {
            // cache-miss, populate availableLocalSensors
            Log.d(TAG, "getAvailableLocalSensors: building availableLocalSensors");
            
            availableLocalSensors = new HashSet<WaveSensor>();
            
            // It is a nuissance to reflectively find the WaveSensor
            // subclasses, and Package.getAnnotations() is not implemented by
            // Android yet, so that is ruled out.
            // So, for the purpose of the base sensors, we name their classes
            // explicitly here.  While this is fragile, it is straightforward
            // with no additional overhead
            List<Class> waveSensorClasses = new ArrayList<Class>();
            waveSensorClasses.add(AndroidHardwareAccelerometer.class);
            waveSensorClasses.add(AndroidHardwareMagneticField.class);
            waveSensorClasses.add(AndroidLocationSensor.class);
            
            for (Class aClass : waveSensorClasses) {
                assert aClass.isAssignableFrom(WaveSensor.class) : aClass;
                
                Log.d(TAG, "\t"+aClass);
                
                try {
                    Method m = aClass.getMethod("instancesAvailableInContext", Context.class);
                    
                    Set<WaveSensor> theseInstances = (Set<WaveSensor>)m.invoke(aClass, mContext);
                    for (WaveSensor ws : theseInstances) {
                        Log.d(TAG, "\t\t"+ws);
                    }
                    
                    availableLocalSensors.addAll(theseInstances);
                } catch (NoSuchMethodException nsme) {
                    Log.w(TAG, ""+aClass, nsme);
                } catch (IllegalAccessException iae) {
                    Log.w(TAG, ""+aClass, iae);
                } catch (InvocationTargetException ite) {
                    Log.w(TAG, ""+aClass, ite);
                }
            }
        }
        return availableLocalSensors;
    }
    
    /**
     * availableSensorsMatchingWaveSensorDescription
     * 
     * @see getAvailableLocalSensors
     */
    public Set<WaveSensor> availableSensorsMatchingWaveSensorDescription(WaveSensorDescription sensorDescription) {
        
        HashSet<WaveSensor> matchingSensors = new HashSet<WaveSensor>();
        
        for (WaveSensor candidateSensor : this.getAvailableLocalSensors()) {
            if (candidateSensor.matchesWaveSensorDescription(sensorDescription)) {
                matchingSensors.add(candidateSensor);
            }
        }
        
        return matchingSensors;
    }
    
    /**
     * supportInfoForRecipe
     * 
     * provides information about the sensors which this device offers to
     * support a given recipe.  Currently if a device has multiple local
     * sensors of the same time, there is no particular order or ranking to
     * that portion of the matching.
     * 
     * TODO: Add support for conforming units, as currently they must match
     *       exactly
     * TODO: Add support for non-local sensors
     */
    public WaveRecipeLocalDeviceSupportInfo supportInfoForRecipe(WaveRecipe recipe) {
        WaveRecipeLocalDeviceSupportInfo supportInfo = new WaveRecipeLocalDeviceSupportInfo(recipe);
        
        boolean allSensorsSatisfied = true;
        Set<WaveSensor> availableSensors = getAvailableLocalSensors();
        // this is an inefficient inner loop, but the number of sensors is
        // expected to be small
        for (WaveSensorDescription wsd : recipe.getSensors()) {
            boolean thisSensorSatisfied = false;
            for (WaveSensor s : availableSensors) {
                if (s.matchesWaveSensorDescription(wsd)) {
                    thisSensorSatisfied = true;
                    // store information for this sensor in the supportInfo
                    supportInfo.getDescriptionToSensorMap().put(wsd, s);
                    break;
                }
            }
            allSensorsSatisfied &= thisSensorSatisfied;
        }
        
        supportInfo.setSupported(allSensorsSatisfied);
        
        return supportInfo;
    }
    
    /**
     * scheduleAuthorization
     * 
     * For the time being, we will not allow updating of the schedule, as we
     * are not yet adjusting recipes on the fly.
     * 
     * TODO: return void and throw Exceptions
     */
    public boolean scheduleAuthorization(WaveRecipeAuthorization authorization, WaveRecipeOutputListener listener)
            throws SensorNotAvailableException {
        
        // DEBUG
        // try {
        //     Debug.startMethodTracing("androidwave");
        // } catch (Exception e) {
        //     Log.d(TAG, "Exception while Debug.startMethodTracing(...)", e);
        // }
        
        if (scheduledAuthorizations.containsKey(authorization)) {
            return false;
        }
        
        // first check that the authorization is supported on the current hardware
        WaveRecipeLocalDeviceSupportInfo supportInfo = supportInfoForRecipe(authorization.getRecipe());
        if (!supportInfo.isSupported()) {
            throw new SensorNotAvailableException();
        }
        
        WaveRecipeAlgorithm algorithmInstance;
        try {
            algorithmInstance = authorization.getRecipe().getAlgorithmInstance();
            authorizationToAlgorithmMap.put(authorization, algorithmInstance);
        } catch (Exception e) {
            Log.d(TAG, "Exception while getting algorithm instance for authorization", e);
            return false;
        }
        
        // link up the output of the algorithm instance
        double maxRate = 0.0;
        double maxPrecision = 0.0;
        try {
            maxRate = authorization.getOutputRate();
            maxPrecision = authorization.getOutputPrecision();
        } catch (Exception e) {
            Log.w(TAG, "Exception while getting authorization output rates", e);
            return false;
        }
        algorithmInstance.setWaveRecipeAlgorithmListener(new AlgorithmOutputForwarder(maxRate, maxPrecision, authorization, listener));
        
        // start the sensors needed for this authorization
        boolean allStarted = true;
        for (Map.Entry<WaveSensorDescription, WaveSensor> entry : supportInfo.getDescriptionToSensorMap().entrySet()) {
            WaveSensorDescription wsd = entry.getKey();
            WaveSensor ws = entry.getValue();
            
            SensorAttributes sa = authorization.getSensorAttributesForSensor(wsd);
            double requestedRate = sa.rate;
            double requestedPrecision = sa.precision;
            
            try {
                ws.registerListener(algorithmInstance, wsd, requestedRate, requestedPrecision);
            } catch (Exception e) {
                Log.d(TAG, "Exception while starting sensor", e);
                allStarted = false;
            }
        }
        
        // all sensors for this authorization should be started at this point
        scheduledAuthorizations.put(authorization, supportInfo);
        
        return allStarted;
    }
    
    public boolean descheduleAuthorization(WaveRecipeAuthorization authorization) {
        
        // DEBUG
        // try {
        //     Debug.stopMethodTracing();
        // } catch (Exception e) {
        //     Log.d(TAG, "Exception while Debug.stopMethodTracing()", e);
        // }
        
        WaveRecipeLocalDeviceSupportInfo supportInfo = scheduledAuthorizations.get(authorization);
        if (supportInfo == null) {
            return false;
        }
        
        // stop the sensors needed for this authorization
        for (WaveSensor ws : supportInfo.getDescriptionToSensorMap().values()) {
            try {
                ws.unregisterListener(authorizationToAlgorithmMap.remove(authorization));
            } catch (Exception e) {
                Log.d(TAG, "Exception while stopping sensor", e);
            }
        }
        scheduledAuthorizations.remove(authorization);
        return true;
    }
}