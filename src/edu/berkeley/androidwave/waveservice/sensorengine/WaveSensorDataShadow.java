// 
//  WaveSensorDataShadow.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-05.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice.sensorengine;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * WaveSensorDataShadow
 * 
 * TODO: move this to the .waverecipe.waverecipealgorithm package
 */
public class WaveSensorDataShadow extends WaveSensorData {
    
    Object dataImpl;
    
    Method implGetTimeMethod;
    Method implHasChannelNameMethod;
    Method implGetChannelValueMethod;
    
    public WaveSensorDataShadow(Object impl) throws Exception {
        // make sure that impl acts like WaveSensorData
        
        HashSet<String> theseMethods = new HashSet<String>();
        Method[] methods = WaveSensorData.class.getMethods();
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
            if (name.equals("getTime")) {
                implGetTimeMethod = m;
            } else if (name.equals("hasChannelName")) {
                implHasChannelNameMethod = m;
            } else if (name.equals("getChannelValue")) {
                implGetChannelValueMethod = m;
            }
        }
        
        if (!thoseMethods.containsAll(theseMethods)) {
            throw new Exception(""+impl+" does not act like WaveSensorData");
        }
        
        dataImpl = impl;
    }
    
    @Override
    public long getTime() {
        Object returnVal;
        try {
            returnVal = implGetTimeMethod.invoke(dataImpl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Long)returnVal).longValue();
    }
    
    @Override
    public boolean hasChannelName(String name) {
        Object returnVal;
        try {
            returnVal = implHasChannelNameMethod.invoke(dataImpl, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Boolean)returnVal).booleanValue();
    }
    
    @Override
    public double getChannelValue(String name) {
        Object returnVal;
        try {
            returnVal = implGetTimeMethod.invoke(dataImpl, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Double)returnVal).doubleValue();
    }
}