// 
//  WaveRecipeAlgorithmListenerShadow.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-05.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.waverecipealgorithm;

import java.lang.reflect.Method;
import java.util.HashSet;

public class WaveRecipeAlgorithmListenerShadow implements WaveRecipeAlgorithmListener {
    Object listenerImpl;
    
    Method implHandleRecipeDataMethod;
    
    public WaveRecipeAlgorithmListenerShadow(Object impl) throws Exception {
        // make sure that impl implements WaveRecipeAlgorithmListener
        
        HashSet<String> theseMethods = new HashSet<String>();
        Method[] methods = WaveRecipeAlgorithmListener.class.getMethods();
        for (Method m : methods) {
            theseMethods.add(m.getName());
        }
        
        HashSet<String> thoseMethods = new HashSet<String>();
        Method[] implMethods = impl.getClass().getMethods();
        for (Method m : implMethods) {
            String name = m.getName();
            thoseMethods.add(name);
            
            if (name.equals("handleRecipeData")) {
                implHandleRecipeDataMethod = m;
            }
        }
        
        if (!thoseMethods.containsAll(theseMethods)) {
            throw new Exception(""+impl+" does not implement WaveRecipeAlgorithmListener");
        }
        
        listenerImpl = impl;
    }
    
    public void handleRecipeData(WaveRecipeOutputData data) {
        try {
            implHandleRecipeDataMethod.invoke(listenerImpl, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // TODO: also shadow toString, etc.
}