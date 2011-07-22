// 
//  WaveRecipeAlgorithmShadow.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-15.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.*;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

class WaveRecipeAlgorithmShadow implements WaveRecipeAlgorithm {
    
    private static final String TAG = WaveRecipeAlgorithmShadow.class.getSimpleName();
    
    Object algorithmImpl;
    
    Method implSetWaveRecipeAlgorithmListenerMethod;
    Method implIngestSensorDataMethod;
    
    WaveRecipeAlgorithmShadow(Object impl) throws Exception {
        // make sure that impl implements WaveRecipeAlgorithm
        
        // first get the local WaveRecipeAlgorithm Method Names
        HashSet<String> theseMethods = new HashSet<String>();
        Method[] methods = WaveRecipeAlgorithm.class.getMethods();
        for (Method m : methods) {
            theseMethods.add(m.getName());
        }
        
        // then get those of impl
        HashSet<String> thoseMethods = new HashSet<String>();
        Method[] implMethods = impl.getClass().getMethods();
        for (Method m : implMethods) {
            String name = m.getName();
            thoseMethods.add(name);
            // and cache important methods
            if (name.equals("setWaveRecipeAlgorithmListener")) {
                implSetWaveRecipeAlgorithmListenerMethod = m;
            } else if (name.equals("ingestSensorData")) {
                implIngestSensorDataMethod = m;
            }
        }
        
        if (!thoseMethods.containsAll(theseMethods)) {
            throw new Exception(""+impl+" does not implement WaveRecipeAlgorithm");
        }
        
        algorithmImpl = impl;
    }
    
    /**
     * There are only two methods to shadow, so this class is currently small
     */
    
    public boolean setWaveRecipeAlgorithmListener(Object listener) throws Exception {
        // Log.v(TAG, "setWaveRecipeAlgorithmListener("+listener+")");
        Object returnVal;
        returnVal = implSetWaveRecipeAlgorithmListenerMethod.invoke(algorithmImpl, listener);
        return ((Boolean)returnVal).booleanValue();
    }
    
    public void ingestSensorData(long time, Map<String, Double> values) throws Exception {
        // Log.v(TAG, "ingestSensorData("+time+", "+values+")");
        try {
            implIngestSensorDataMethod.invoke(algorithmImpl, time, values);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof Exception) {
                throw (Exception)cause;
            } else {
                throw new Exception(ite);
            }
        }
    }
    
    /**
     * TODO: also shadow toString, etc.
     */
}