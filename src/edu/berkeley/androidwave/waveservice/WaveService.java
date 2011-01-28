package edu.berkeley.calfitwave.waveservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import edu.berkeley.calfitwave.waverecipe.*;

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
    
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MAIN)) {
            return mPublicBinder;
        }
        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            return mUIBinder;
        }
        return null;
    }
    
    /**
     * The UI service is a local binding only as there is only one UI app
     * It is identified by the ACTION_EDIT Intent
     */
    private final IBinder mUIBinder = new LocalBinder();
    
    /**
     * The associated local binder
     */
    public class LocalBinder extends Binder {
        WaveService getService() {
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
     * recipes. @see edu.berkeley.calfitwave.waverecipe.WaveRecipe
     */
    WaveRecipeAuthorization[] deviceRecipeAuthorizations() {
        return null;
    }
    
    
    /**
     * deviceChannels
     *
     * Get a list of all outgoing data channels the device supports
     * @see WaveDeviceChannel
     */
    WaveDeviceChannel deviceChannels() {
        return null;
    }
    
    /**
     * WAVESERVICE PUBLIC METHODS
     */
     
     private final IWaveServicePublic.Stub mPublicBinder = new IWaveServicePublic.Stub() {
         /**
          * registerRecipe
          *
          * @see IWaveServicePublic#registerRecipe(WaveRecipe)
          */
         public boolean registerRecipe(WaveRecipe recipe) {
             return false;
         }

         /**
          * requestRecipe
          *
          * @see IWaveServicePublic#requestRecipe(String)
          */
         public WaveRecipeAuthorization requestRecipe(String recipeUID) {
             return null;
         }

         /**
          * registerRecipeOutputListener
          *
          * @see IWaveServicePublic#registerRecipeOutputListener(WaveRecipeOutputListener, boolean)
          */
         public boolean registerRecipeOutputListener(IWaveRecipeOutputListener listener, boolean includeSensorData) {
             return false;
         }
     };
     
}