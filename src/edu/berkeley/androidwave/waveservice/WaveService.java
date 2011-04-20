package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waveclient.IWaveServicePublic;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveexception.WaveRecipeNotCachedException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.*;

import edu.berkeley.androidwave.waverecipe.*;

/**
 * WaveService
 * 
 * This is the primary Wave component, the Wave Service.  It encapsulates all
 * of the main functionality of the wave system, managing sensors, recipes,
 * and synchronization.
 *
 * It provides two primary interfaces, a public interface via AIDL for Wave
 * Client applications, and a private local interface for the WaveUI
 * Application.
 */
public class WaveService extends Service {
    
    public static final String ACTION_WAVE_SERVICE = "edu.berkeley.androidwave.intent.action.WAVE_SERVICE";
    public static final String ACTION_REQUEST_RECIPE_AUTHORIZE = "edu.berkeley.androidwave.intent.action.AUTHORIZE";
    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String WAVERECIPE_CACHE_DIR = "waverecipes/cache";
    
    
    protected void throwNotImplemented() {
        String className = getClass().getName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        throw new RuntimeException(className+"#"+methodName+" not implemented yet!");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(ACTION_WAVE_SERVICE)) {
            return mPublicBinder;
        }
        if (intent.getAction().equals(Intent.ACTION_MAIN)) {
            return mUIBinder;
        }
        return null;
    }
    
    /**
     * The UI service is a local binding only as there is only one UI app
     */
    private final IBinder mUIBinder = new LocalBinder();
    
    /**
     * The associated local binder
     */
    public class LocalBinder extends Binder {
        public WaveService getService() {
            return WaveService.this;
        }
    }
    
    /**
     * UI (LOCAL) METHODS
     */
    
    /**
     * deviceRecipeAuthorizations
     * 
     * Get an array of all recipes in-use as well as locally installed/stored
     * recipes. @see edu.berkeley.androidwave.waverecipe.WaveRecipe
     */
    public WaveRecipeAuthorization[] deviceRecipeAuthorizations() {
        return null;
    }
    
    
    /**
     * deviceChannels
     *
     * Get a list of all outgoing data channels the device supports
     * @see WaveDeviceChannel
     */
    public WaveDeviceChannel deviceChannels() {
        return null;
    }
    
    /**
     * beginRetrieveRecipeForID
     * 
     * this should contact a recipe server, validate (jar has valid sig only)
     * and cache it
     */
    public void beginRetrieveRecipeForID(String id, RecipeRetrievalResponder r) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            // do nothing, we just wanted to simulate a longer method
        }
        r.handleRetrievalFailed(id, "beginRetrieveRecipeForID not implemented yet.");
    }
    
    /**
     * recipeCacheFileForId
     */
    public File recipeCacheFileForId(String id)
            throws WaveRecipeNotCachedException {
        String[] c = WAVERECIPE_CACHE_DIR.split(File.separator, 2);
        File filesDir = getDir(c[0], Context.MODE_PRIVATE);
        File recipeFile = new File(filesDir, (c.length == 1 ? "" : c[1] + "/")+id+".waverecipe");
        if (!recipeFile.exists()) {
            throw new WaveRecipeNotCachedException(id);
        }
        return recipeFile;
    }

    
    /**
     * WAVESERVICE PUBLIC METHODS
     * 
     * @see IWaveServicePublic
     */
     private final IWaveServicePublic.Stub mPublicBinder = new IWaveServicePublic.Stub() {
         
         /**
          * isAuthorized
          */
         public boolean isAuthorized(String recipeID) {
             return false;
         }

         /**
          * retrieveAuthorization
          */
         public WaveRecipeAuthorizationInfo retrieveAuthorizationInfo(String recipeID) {
             return null;
         }
         
         /**
          * getAuthorizationIntent
          */
         public Intent getAuthorizationIntent(String recipeID) {
             Intent authIntent = new Intent(ACTION_REQUEST_RECIPE_AUTHORIZE);
             authIntent.putExtra(RECIPE_ID_EXTRA, recipeID);
             return authIntent;
         }

         /**
          * registerRecipeOutputListener
          */
         public boolean registerRecipeOutputListener(String recipeId, IWaveRecipeOutputDataListener listener) {
             throwNotImplemented();
             return false;
         }
         
         /**
          * unregisterRecipeOutputListener
          */
         public boolean unregisterRecipeOutputListener(String recipeId, IWaveRecipeOutputDataListener listener) {
             throwNotImplemented();
             return false;
         }
     };
     
}