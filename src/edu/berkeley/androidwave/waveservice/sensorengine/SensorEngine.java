// 
//  SensorEngine.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-08.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
//  
//  Uses portions developed by Mr. Charles Wang <charleswang007@gmail.com>
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import edu.berkeley.androidwave.waveexception.SensorNotAvailableException;
import edu.berkeley.androidwave.waverecipe.granularitytable.SensorAttributes;
import edu.berkeley.androidwave.waverecipe.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.*;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SensorEngine
 * 
 * SensorEngine is a singleton, because there is only one set of underlying
 * hardware sensors.
 */
public class SensorEngine implements SensorEventListener {
    
    private static final String TAG = SensorEngine.class.getSimpleName();
    
    protected static SensorEngine theInstance;
    
    protected Context mContext;
    
    protected SensorManager mSensorManager;
    
    class AuthorizationStats {
        AuthorizationStats(WaveRecipeLocalDeviceSupportInfo supportInfo) {
            this.supportInfo = supportInfo;
            sensorToDescriptionMap = new HashMap<Sensor, WaveSensorDescription>();
            lastSampleTimes = new HashMap<WaveSensorDescription, Long>();
        }
        public WaveRecipeLocalDeviceSupportInfo supportInfo;
        public WaveRecipeAlgorithm algorithmInstance;
        public HashMap<Sensor, WaveSensorDescription>sensorToDescriptionMap;  // an inversion of supportInfo.descriptionToSensorMap
        public HashMap<WaveSensorDescription, Long>lastSampleTimes;
    }
    
    class SensorStats {
        SensorStats(int sensorManagerRate, double desiredRate) {
            this.sensorManagerRate = sensorManagerRate;
            this.desiredRate = desiredRate;
        }
        public int sensorManagerRate;
        public double desiredRate;
        public long lastSampleTime;
        public double estimatedRate;
    }
    
    class AlgorithmOutputForwarder implements WaveRecipeAlgorithmListener {
        long lastForwardTime;
        double maxOutputRate;
        double maxOutputPrecision;
        WaveRecipeAuthorization authorization;
        WaveRecipeOutputListener destination;
        
        AlgorithmOutputForwarder(double rate, double precision, WaveRecipeAuthorization auth, WaveRecipeOutputListener dest) {
            lastForwardTime = 0;
            maxOutputRate = rate;
            maxOutputPrecision = precision;
            authorization = auth;
            destination = dest;
        }
        
        public void handleRecipeData(WaveRecipeOutputData data) {
            // drop this data if it exceeds the max rate
            // TODO: Consider SystemClock.elapsedRealTime() in place of System.currentTimeMillis()
            long now = System.currentTimeMillis();
            double thisRate = 1000.0 / (now - lastForwardTime);
            if (thisRate < maxOutputRate) {
                // rate is good, truncate precision
                data.quantize(maxOutputPrecision);
                destination.receiveDataForAuthorization(data, authorization);
            } else {
                Log.d(TAG, "Dropped excessive recipe output");
            }
        }
    }
    
    
    protected Map<WaveRecipeAuthorization, AuthorizationStats> scheduledAuthorizations;
    protected Map<Sensor, SensorStats> runningSensors;
    
    /**
     * Private Constructor for Singleton
     */
    private SensorEngine(Context c) {
        mContext = c;
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        
        scheduledAuthorizations = new HashMap<WaveRecipeAuthorization, AuthorizationStats>();
        runningSensors = new HashMap<Sensor, SensorStats>();
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
    
    /**
     * availableSensorsMatchingWaveSensorDescription
     * 
     * @see WaveSensor#getAvailableLocalSensors
     */
    public Set<WaveSensor> availableSensorsMatchingWaveSensorDescription(WaveSensorDescription sensorDescription)
            throws Exception {
        
        HashSet<WaveSensor> matchingSensors = new HashSet<WaveSensor>();
        
        Set<WaveSensor> availableLocalSensors = WaveSensor.getAvailableLocalSensors(mContext);
        
        for (WaveSensor candidateSensor : availableLocalSensors) {
            
            WaveSensorDescription.Type targetType = sensorDescription.getType();
            if (candidateSensor.getType() == targetType) {
                if (sensorDescription.hasChannels()) {
                    // channel descriptions are present, so they must match
                    throw new Exception("not implemented yet");
                } else if (sensorDescription.hasExpectedUnits()) {
                    String expectedUnits = sensorDescription.getExpectedUnits();
                    if (candidateSensor.getUnits().equals(expectedUnits)) {
                        matchingSensors.add(candidateSensor);
                    }
                } else {
                    matchingSensors.add(candidateSensor);
                }
            }
        }
        
        return matchingSensors;
    }
    
    /**
     * startSensor
     * 
     * starts a sensor targeting a given sampling rate.  No scheduleing is
     * done here to support multiple recipes directly (hence a protected
     * method).  Not synchronized because it is not a public method.
     *
     * TODO: specialize this exception
     * 
     * Uses frequency mapping generated by
     * Mr. Charles Wang <charleswang007@gmail.com>
     */
    protected void startSensor(Sensor sensor, double rate) throws Exception {
        if (runningSensors.containsKey(sensor)) {
            throw new Exception("Sensor "+sensor+" already started.");
        }
        
        int sensorManagerRate = SensorManager.SENSOR_DELAY_NORMAL;
        if (rate < 5.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_NORMAL;
        } else if (rate < 8.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_UI;
        } else if (rate < 12.0) {
            sensorManagerRate = SensorManager.SENSOR_DELAY_GAME;
        } else {
            sensorManagerRate = SensorManager.SENSOR_DELAY_FASTEST;
        }
        
        mSensorManager.registerListener(this, sensor, sensorManagerRate);
        SensorStats ss = new SensorStats(sensorManagerRate, rate);
        runningSensors.put(sensor, ss);
    }
    
    /**
     * stopSensor
     * 
     * @see #startAndroidWaveSensor
     */
    protected void stopSensor(Sensor sensor) throws Exception {
        if (!runningSensors.containsKey(sensor)) {
            throw new Exception("Sensor "+sensor+" not started.");
        }

        runningSensors.remove(sensor);
        mSensorManager.unregisterListener(this);
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
     */
    public WaveRecipeLocalDeviceSupportInfo supportInfoForRecipe(WaveRecipe recipe) {
        WaveRecipeLocalDeviceSupportInfo supportInfo = new WaveRecipeLocalDeviceSupportInfo(recipe);
        
        boolean allSensorsSatisfied = true;
        Set<WaveSensor> availableSensors = WaveSensor.getAvailableLocalSensors(mContext);
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
        
        if (scheduledAuthorizations.containsKey(authorization)) {
            return false;
        }
        
        // first check that the authorization is supported on the current hardware
        WaveRecipeLocalDeviceSupportInfo supportInfo = supportInfoForRecipe(authorization.getRecipe());
        if (!supportInfo.isSupported()) {
            throw new SensorNotAvailableException();
        }
        
        AuthorizationStats as = new AuthorizationStats(supportInfo);
        try {
            as.algorithmInstance = authorization.getRecipe().getAlgorithmInstance();
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
        as.algorithmInstance.setWaveRecipeAlgorithmListener(new AlgorithmOutputForwarder(maxRate, maxPrecision, authorization, listener));
        
        // start the sensors needed for this authorization
        for (Map.Entry<WaveSensorDescription, WaveSensor> entry : supportInfo.getDescriptionToSensorMap().entrySet()) {
            WaveSensorDescription wsd = entry.getKey();
            WaveSensor ws = entry.getValue();
            if (ws instanceof AndroidWaveSensor) {
                AndroidWaveSensor aws = (AndroidWaveSensor) ws;
                // store the hardware sensor to sensor discription pairing for quick reference
                as.sensorToDescriptionMap.put(aws.getAndroidSensor(), wsd);
                // first, recall from the authorization the appropriate rate for this sensor
                SensorAttributes sa = authorization.getSensorAttributesForSensor(wsd);
                double requestedRate = sa.rate;
                // now that we have discovered the requested rate, see if the
                // matching hardware sensor is already running
                if (runningSensors.containsKey(ws)) {
                    // sensor is already running
                    SensorStats ss = runningSensors.get(ws);
                    // TODO: improve allocation algorithm
                    if (ss.desiredRate < requestedRate) {
                        // need to reschedule the sensor
                        try {
                            this.stopSensor(aws.getAndroidSensor());
                            this.startSensor(aws.getAndroidSensor(), requestedRate);
                        } catch (Exception e) {
                            Log.w(TAG, "Exception while restarting sensor", e);
                        }
                    }
                } else {
                    // sensor is not running, start it at the requested rate
                    // this just sets a guess at the actual hardware rate
                    // anyway.
                    try {
                        this.startSensor(aws.getAndroidSensor(), requestedRate);
                    } catch (Exception e) {
                        Log.w(TAG, "Exception while starting sensor", e);
                    }
                }
            } else {
                // TODO: refactor this portion of scheduling into the WaveSensor implementation itself, so that location sensing, etc. can be scheduled.
                throw new SensorNotAvailableException("Only AndroidWaveSensors can be scheduled at this time");
            }
        }
        // all sensors for this authorization should be started at this point
        scheduledAuthorizations.put(authorization, as);
        
        return true;
    }
    
    public boolean descheduleAuthorization(WaveRecipeAuthorization authorization) {
        if (!scheduledAuthorizations.containsKey(authorization)) {
            return false;
        }
        
        // we do not stop sensors here, we stop in onSensorChanged if there is
        // no place left to forward the data
        scheduledAuthorizations.remove(authorization);
        return true;
    }
    
    
    /**
     * --------------------- SensorEventListener Methods ---------------------
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // null implementation
    }
    
    public void onSensorChanged(SensorEvent event) {
        // first update sensor stats for this sensor
        SensorStats ss = runningSensors.get(event.sensor);
        long now = System.currentTimeMillis();
        long last = ss.lastSampleTime;
        ss.lastSampleTime = now;
        ss.estimatedRate = 1000.0 / (now - last);
        
        // check which authorizations are relevant, then feed the throttled
        // data to the algorithm instances.
        // TODO: feed on separate threads
        // TODO: optimize the lookups
        boolean hasResponder = false;
        for (Map.Entry<WaveRecipeAuthorization, AuthorizationStats> entry : scheduledAuthorizations.entrySet()) {
            WaveRecipeAuthorization auth = entry.getKey();
            AuthorizationStats stats = entry.getValue();
            
            // look up the authorized rate for this sensor in this authorization
            // (should be null if the recipe for this sensor was unscheduled)
            WaveSensorDescription wsd = stats.sensorToDescriptionMap.get(event.sensor);
            if (wsd != null) {
                hasResponder = true;
                SensorAttributes sa = auth.getSensorAttributesForSensor(wsd);
                // decide whether to dispatch data
                // we have a simple dropping scheme for now to avoid delivering
                // faster than the authorized rate
                Long lastDeliveredTime = stats.lastSampleTimes.get(wsd);
                boolean shouldSend = true;
                if (lastDeliveredTime != null) {
                    double rateForThis = 1000.0 / (now - lastDeliveredTime.longValue());
                    if (rateForThis > sa.rate) {
                        shouldSend = false;
                    }
                }
                // now dispatch the data if necessary
                if (shouldSend) {
                    // store this timestamp
                    stats.lastSampleTimes.put(wsd, new Long(now));
                    // package the sensor data
                    Map<String, Double> values = new HashMap<String, Double>();
                    // for now simple channel handling
                    // TODO: better channel handling
                    WaveSensorChannelDescription[] wscds = wsd.getChannels();
                    for (int i=0; i<4; i++) {
                        if (wscds.length > i && event.values.length > i) {
                            // clip the precision of the delivered value
                            double truncatedValue = event.values[i];
                            long factor = (long)(truncatedValue / sa.precision);
                            truncatedValue = ((double)factor) * sa.precision;
                            values.put(wscds[i].getName(), truncatedValue);
                        }
                    }
                    // call up the algorithmInstance of the authorization
                    stats.algorithmInstance.ingestSensorData(new WaveSensorData(now, values));
                }
            }
        }
        
        if (hasResponder) {
            // sensor should stay alive, adjust its schedule if its rate is
            // too low
            if (ss.estimatedRate < 0.9 * ss.desiredRate) {
                // TODO: increase sensor rate
                Log.w(TAG, ""+event.sensor+" estimated rate less than 90% of desired rate ("+ss.estimatedRate+" < 0.9 * "+ss.desiredRate+")");
            }
        } else {
            // no authorizations require this sensor, so shut it down
            Log.d(TAG, "No listeners for "+event.sensor+", stopping.");
            try {
                this.stopSensor(event.sensor);
            } catch (Exception e) {
                Log.w(TAG, "Exception while stopping sensor", e);
            }
        }
    }
}