// 
//  WaveRecipe.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.InvalidSignatureException;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;

import java.io.*;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.jar.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

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
    
    private static final String DESCRIPTION_XML_PATH = "assets/description.xml";
    
    protected String recipeId;
    protected Date version;
    protected String name;
    protected String description;
    
    /**
     * createFromUID
     * 
     * This should check for a cached version of the recipe, downloading it if
     * necessary, then instantiating from the downloaded version.
     */
    public static WaveRecipe createFromID(String recipeID, int version) {
        // null implementation
        return null;
    }
    
    /**
     * createFromDisk
     *
     * instantiate and return a WaveRecipe from an on disk location.  Should
     * throw an exception if the .waverecipe signature is invalid.
     */
    protected static WaveRecipe createFromDisk(String recipePath)
        throws Exception {
        
        // recipePath should point to an apk.  We need to examine the
        // signature of the apk somehow
        
        // for now we just see if it has a signature
        JarFile recipeApk = new JarFile(recipePath, true);
        if (recipeApk == null) {
            throw new InvalidSignatureException();
        }
        
        // Create a loader for this apk
        dalvik.system.PathClassLoader recipePathClassLoader =
            new dalvik.system.PathClassLoader(recipePath, ClassLoader.getSystemClassLoader());
        
        // try to load the description.xml
        InputStream descriptionInputStream = recipePathClassLoader.getResourceAsStream(DESCRIPTION_XML_PATH);
        //Xml.parse(descriptionInputStream, UTF_8, myContentHandler);
        
        String implementationClassName = "";
        
        // try to load the WaveRecipeAlgorithm implementation
        try {
            Class<?> implementationClass = Class.forName(implementationClassName, true, recipePathClassLoader);
        } catch (ClassNotFoundException cnfe) {
            
        }
        
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
     * getID
     */
    public String getID() {
        return recipeId;
    }
    
    /**
     * getVersion
     */
    public Date getVersion() {
        return version;
    }
    
    /**
     * getName
     */
    public String getName() {
        return name;
    }
    
    /**
     * getDescription
     */
    public String getDescription() {
        return description;
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