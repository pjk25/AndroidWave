// 
//  SensorNotAvailableException.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-05-04.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveexception;

public class SensorNotAvailableException extends Exception {
    
    public SensorNotAvailableException() {
        super();
    }
    
    public SensorNotAvailableException(String message) {
        super(message);
    }
}