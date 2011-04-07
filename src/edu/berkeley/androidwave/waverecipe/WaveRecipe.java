// 
//  WaveRecipe.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-24.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.*;
import edu.berkeley.androidwave.waveclient.WaveSensorDescription;
import edu.berkeley.androidwave.waveclient.WaveSensorChannelDescription;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensorData;

import android.content.Context;
import android.content.pm.*;
import android.util.Log;
import android.util.Xml;
import java.io.*;
import java.security.cert.Certificate;
import java.util.Date;
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
public class WaveRecipe {
    
    private static final String DESCRIPTION_XML_PATH = "assets/description.xml";
    
    protected String recipeId;
    protected Date version;
    protected String name;
    protected String description;
    
    protected WaveSensorDescription[] sensors;
    protected WaveRecipeOutput[] recipeOutputs;
    
    protected GranularityTable granularityTable;
    
    protected Class<WaveRecipeAlgorithm> algorithmMainClass;
    
    /**
     * createFromDisk
     *
     * instantiate and return a WaveRecipe from an on disk location.  Should
     * throw an exception if the .waverecipe signature is invalid.
     */
    public static WaveRecipe createFromDisk(Context context, String recipePath)
        throws Exception {
        
        // recipePath should point to an apk.  We need to examine the
        // signature of the apk somehow
        
        // for now we just see if it has a signature
        JarFile recipeApk = new JarFile(recipePath, true);
        if (recipeApk == null) {
            throw new InvalidSignatureException();
        }
        
        // Use the packagemanager to inspect the apk signature
        PackageInfo recipePackageInfo = context.getPackageManager().getPackageArchiveInfo(recipePath, PackageManager.GET_SIGNATURES);
        Signature[] recipeSignatures = recipePackageInfo.signatures;
        Log.d("WaveRecipe", "Loading recipe at "+recipePath);
        if (recipeSignatures == null || recipeSignatures.length == 0) {
            throw new InvalidSignatureException();
        }
        for (Signature sig : recipeSignatures) {
            Log.d("WaveRecipe", "\t"+sig);
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
     * {@see WaveSensorDescription}
     */
    public WaveSensorDescription[] getSensors() {
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
}