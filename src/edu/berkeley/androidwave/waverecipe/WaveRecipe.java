// 
//  WaveRecipe.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.*;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithmListener;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.jar.JarFile;

/**
 * WaveRecipe
 * 
 * In memory representation of the recipe as provided by an authority.
 * 
 * Recipes are distributed as signed Android packages (.apk files), leveraging
 * the existing Android build and packaging system.  A recipe apk, denoted by
 * the .waverecipe extension, contains assets/description.xml, and
 * classes.dex.  description.xml contains recipe metadata, including the
 * fully qualified names of relevent classes contained in classes.dex.
 */
public class WaveRecipe implements Parcelable {
    
    private static final String DESCRIPTION_XML_PATH = "assets/description.xml";
    
    protected String recipeId;
    protected Date version;
    protected String name;
    protected String description;
    
    protected WaveSensor[] sensors;
    protected WaveRecipeOutput[] recipeOutputs;
    
    protected GranularityTable granularityTable;
    
    protected Class<WaveRecipeAlgorithm> algorithmMainClass;
    
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
        
        WaveRecipe recipe = new WaveRecipe();
        
        // Create a loader for this apk
        // http://yenliangl.blogspot.com/2009/11/dynamic-loading-of-classes-in-your.html
        // http://www.mail-archive.com/android-developers@googlegroups.com/msg07714.html
        dalvik.system.PathClassLoader recipePathClassLoader =
            new dalvik.system.PathClassLoader(recipePath, ClassLoader.getSystemClassLoader());
        
        // try to load the description.xml
        InputStream descriptionInputStream = recipePathClassLoader.getResourceAsStream(DESCRIPTION_XML_PATH);
        WaveRecipeXmlContentHandler contentHandler = new WaveRecipeXmlContentHandler(recipe);
        Xml.parse(descriptionInputStream, Xml.Encoding.UTF_8, contentHandler);
        
        String implementationClassName = contentHandler.getAlgorithmClassName();
        
        // try to load the WaveRecipeAlgorithm implementation
        try {
            recipe.algorithmMainClass = (Class<WaveRecipeAlgorithm>)Class.forName(implementationClassName, true, recipePathClassLoader);
        } catch (ClassNotFoundException cnfe) {
            throw new Exception("Could not find main recipe class "+implementationClassName+". Permissions for /data/dalvik-cache may be incorrect.");
        }
        
        return recipe;
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
     * -------------------------- Instance Methods ---------------------------
     */
    
    /**
     * WaveRecipe
     * 
     * Constructor
     */
    public WaveRecipe() {
        // initialize any complex fields
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
     * getSensors
     * 
     * {@see WaveSensor}
     */
    public WaveSensor[] getSensors() {
        return sensors;
    }
    
    /**
     * getRecipeOutputs
     * 
     * @see WaveRecipeOutput
     */
    public WaveRecipeOutput[] getRecipeOutputs() {
        return recipeOutputs;
    }
    
    /**
     * getGranularityTable
     * 
     * @see GranularityTable
     */
    public GranularityTable getGranularityTable() {
        return granularityTable;
    }
    
    /**
     * getAlgorithmInstance
     */
    public WaveRecipeAlgorithm getAlgorithmInstance()
            throws Exception {
        
        WaveRecipeAlgorithmShadow shadow = new WaveRecipeAlgorithmShadow(algorithmMainClass.newInstance());
        
        return shadow;
    }
    
    /**
     * toString
     */
    @Override
    public String toString() {
        return String.format("%s(%s-%s)", this.getClass().getSimpleName(), recipeId, version);
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


class WaveRecipeAlgorithmShadow implements WaveRecipeAlgorithm {
    
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
    
    public boolean setWaveRecipeAlgorithmListener(WaveRecipeAlgorithmListener listener) {
        Object returnVal;
        try {
            returnVal = implSetWaveRecipeAlgorithmListenerMethod.invoke(algorithmImpl, listener);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ((Boolean)returnVal).booleanValue();
    }
    
    public void ingestSensorData(WaveSensorData sensorData) {
        try {
            implIngestSensorDataMethod.invoke(algorithmImpl, sensorData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * TODO: also shadow toString, etc.
     */
}
