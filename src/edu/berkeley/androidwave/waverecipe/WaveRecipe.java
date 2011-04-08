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
import java.lang.ref.WeakReference;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
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
    
    private static final String X509_TYPE = "X.509";
    private static final String DESCRIPTION_XML_PATH = "assets/description.xml";
    
    // for use in loading signatures
    private static final Object mSync = new Object();
    private static WeakReference<byte[]> mReadBuffer;
    
    protected String recipeId;
    protected Date version;
    protected String name;
    protected String description;
    
    // for the AndroidManifest.xml
    protected X509Certificate certificate;
    
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
        
        // final Object mSync = new Object();
        // WeakReference<byte[]> mReadBuffer;
        
        WeakReference<byte[]> readBufferRef;
        byte[] readBuffer = null;
        synchronized (mSync) {
            readBufferRef = mReadBuffer;
            if (readBufferRef != null) {
                mReadBuffer = null;
                readBuffer = readBufferRef.get();
            }
            if (readBuffer == null) {
                readBuffer = new byte[8192];
                readBufferRef = new WeakReference<byte[]>(readBuffer);
            }
        }
        
        // Check the signatures of the recipe (which is an APK, which is a Jar)
        JarFile recipeApk = new JarFile(recipePath);
        if (recipeApk == null) {
            throw new InvalidSignatureException("Recipe signatures did not verify");
        }
        
        // we set sigsFailed false if just one signature is found.
        JarEntry entry = recipeApk.getJarEntry("AndroidManifest.xml");
        if (entry == null) {
            throw new InvalidSignatureException("Recipe has no AndroidManifest.xml");
        }
        Log.d("WaveRecipe", "Looking for signatures in "+recipePath+":"+entry.getName());
        // http://androidcracking.blogspot.com/2010/12/getting-apk-signature-outside-of.html
        Certificate[] certs = loadCertificates(recipeApk, entry, readBuffer);
        if (certs == null || certs.length == 0) {
            throw new InvalidSignatureException("No signatures found for AndroidManifest.xml");
        }
        Log.d("WaveRecipe", "Discovered signature of type: "+certs[0].getType());
        if (!certs[0].getType().equals(X509_TYPE)) {
            throw new InvalidSignatureException("AndroidManifest.xml signature is not an X509 Certificate");
        }
        
        // Recipe is verified and has at least one signature on the AndroidManifest.xml
        WaveRecipe recipe = new WaveRecipe();
        
        recipe.certificate = (X509Certificate)certs[0];
        
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
     * getCertificates
     */
    public X509Certificate getCertificate() {
        return certificate;
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
    
    /**
     * Private Methods
     */
    
    /**
     * loadSignatures
     * 
     * from http://androidcracking.blogspot.com/2010/12/getting-apk-signature-outside-of.html
     */
    private static java.security.cert.Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }
            is.close();

            return (java.security.cert.Certificate[]) (je != null ? je.getCertificates() : null);
        } catch (IOException e) {
            System.err.println("Exception reading " + je.getName() + " in "
                + jarFile.getName() + ": " + e);
        }
        return null;
    }
}