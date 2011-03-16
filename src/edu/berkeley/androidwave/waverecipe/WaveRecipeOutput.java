// 
//  WaveRecipeOutput.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import java.util.Vector;

/**
 * WaveRecipeOutput
 * 
 * General representation of one output from a WaveRecipe
 */
public class WaveRecipeOutput {
    
    protected String name;
    protected String units;
    
    protected Vector<WaveRecipeOutputChannel> channels;
    
    public WaveRecipeOutput(String name, String units) {
        this.name = name;
        this.units = units;
        
        channels = new Vector<WaveRecipeOutputChannel>();
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
    }
    
    /**
     * getUnits
     */
    public String getUnits() {
        return units;
    }
    
    /**
     * getChannels
     */
    public WaveRecipeOutputChannel[] getChannels() {
        return channels.toArray(new WaveRecipeOutputChannel[0]);
    }

    /**
     * addChannel
     */
    public boolean addChannel(WaveRecipeOutputChannel c) {
        return channels.add(c);
    }
}