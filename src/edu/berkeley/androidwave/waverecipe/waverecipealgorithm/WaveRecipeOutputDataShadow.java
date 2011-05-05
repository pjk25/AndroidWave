// 
//  WaveRecipeOutputDataShadow.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-05.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

public class WaveRecipeOutputDataShadow extends WaveRecipeOutputData {
    
    Object outputDataImpl;
    
    Method implGetTimeMethod;
    Method implGetValuesMethod;
    Method implHasChannelNameMethod;
    Method implGetChannelValueMethod;
    Method implSetChannelValueMethod;
    Method implQuantizeMethod;
    
    public WaveRecipeOutputDataShadow(Object impl) throws Exception {
        // make sure that impl acts like WaveRecipeOutputData
        
        HashSet<String> theseMethods = new HashSet<String>();
        Method[] methods = WaveRecipeOutputData.class.getMethods();
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
            } else if (name.equals("getValues")) {
                implGetValuesMethod = m;
            } else if (name.equals("hasChannelName")) {
                implHasChannelNameMethod = m;
            } else if (name.equals("getChannelValue")) {
                implGetChannelValueMethod = m;
            } else if (name.equals("setChannelValue")) {
                implSetChannelValueMethod = m;
            } else if (name.equals("quantize")) {
                implQuantizeMethod = m;
            }
        }
        
        if (!thoseMethods.containsAll(theseMethods)) {
            throw new Exception(""+impl+" does not act like WaveRecipeOutputData");
        }
        
        outputDataImpl = impl;
    }
    
    @Override
    public long getTime() {
        Object returnVal;
        try {
            returnVal = implGetTimeMethod.invoke(outputDataImpl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Long)returnVal).longValue();
    }
    
    @Override
    public Map<String, Double> getValues() {
        Object returnVal;
        try {
            returnVal = implGetValuesMethod.invoke(outputDataImpl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (Map<String, Double>)returnVal;
    }
    
    @Override
    public boolean hasChannelName(String name) {
        Object returnVal;
        try {
            returnVal = implHasChannelNameMethod.invoke(outputDataImpl, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Boolean)returnVal).booleanValue();
    }
    
    @Override
    public double getChannelValue(String name) {
        Object returnVal;
        try {
            returnVal = implGetTimeMethod.invoke(outputDataImpl, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Double)returnVal).doubleValue();
    }
    
    @Override
    public void setChannelValue(String name, double value) {
        try {
            implSetChannelValueMethod.invoke(outputDataImpl, name, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void quantize(double s) {
        try {
            implQuantizeMethod.invoke(outputDataImpl, new Double(s));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}