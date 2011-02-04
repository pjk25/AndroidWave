// 
//  WaveRecipe.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.InvalidSignatureException;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * WaveRecipe
 * 
 * In memory representation of the recipe as provided by an authority.
 * 
 * Recipes are distributed as jarfiles, as they can be
 * readily signed with the jarsigner application.
 *
 * The structure of a .waverecipe is as follows:
 *          MyRecipe.waverecipe -*- description.xml
 *                               - classes.dex
 *
 * description.xml indicates, among other things, the names of specific
 * classes to be loaded in the classes.dex
 * 
 * http://doandroids.com/blogs/2010/6/10/android-classloader-dynamic-loading-of/
 * http://yenliangl.blogspot.com/2009/11/dynamic-loading-of-classes-in-your.html
 * http://download.oracle.com/javase/1.3/docs/tooldocs/win32/jarsigner.html
 */
public class WaveRecipe implements Parcelable {
    
    /**
     * createFromUID
     * 
     * This should check for a cached version of the recipe, downloading it if
     * necessary, then instantiating from the downloaded version.
     */
    public static WaveRecipe createFromID(String recipeID, int version)
        throws InvalidSignatureException {
        // null implementation
        return null;
    }
    
    /**
     * createFromDisk
     *
     * instantiate and return a WaveRecipe from an on disk location.  Should
     * throw an exception if the .waverecipe jar signature is invalid.
     */
    protected static WaveRecipe createFromDisk(String recipePath)
        throws InvalidSignatureException {
        // null implementation
        return null;
    }
    
    /**
     * retrieveRecipe
     * 
     * Retrieve a recipe from a recipe authority.  Note that we need a
     * running recipe server for this.  This should cache the recipe locally.
     */
    protected static boolean retreiveRecipe(String recipeUID)
        throws InvalidSignatureException {
        // null implementation
        return false;
    }
    
    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        
    }
    
    public static final Parcelable.Creator<WaveRecipe> CREATOR = new Parcelable.Creator<WaveRecipe>() {
        public WaveRecipe createFromParcel(Parcel in) {
            return new WaveRecipe(in);
        }
        
        public WaveRecipe[] newArray(int size) {
            return new WaveRecipe[size];
        }
    };
    
    private WaveRecipe(Parcel in) {
        
    }
}