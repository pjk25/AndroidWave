// 
//  InvalidSignatureException.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-02.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveexception;

public class InvalidSignatureException extends Exception {
    
    public InvalidSignatureException() {
        super();
    }
    
    public InvalidSignatureException(String detailMessage) {
        super(detailMessage);
    }
}