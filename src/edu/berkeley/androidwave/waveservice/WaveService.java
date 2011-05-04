// 
//  WaveService.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2010-12-15.
//  Copyright 2010 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waveclient.*;
import edu.berkeley.androidwave.waveexception.WaveRecipeNotCachedException;
import edu.berkeley.androidwave.waveservice.sensorengine.SensorEngine;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveRecipeOutputListener;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
public class WaveService extends Service implements WaveRecipeOutputListener {
    
    private static final String TAG = WaveService.class.getSimpleName();
    
    public static final String ACTION_WAVE_SERVICE = "edu.berkeley.androidwave.intent.action.WAVE_SERVICE";
    public static final String ACTION_REQUEST_RECIPE_AUTHORIZE = "edu.berkeley.androidwave.intent.action.AUTHORIZE";
    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String CLIENT_KEY_EXTRA = "client_key";
    public static final String WAVERECIPE_CACHE_DIR = "waverecipes/cache";
    
    protected RecipeDbHelper databaseHelper;
    
    // Name in these maps refers to the client's package name
    protected Map<String, String> clientKeyNameMap;
    protected Map<String, String> clientNameKeyMap;
    protected ArrayList<WaveRecipeAuthorization> authorizations;
    
    // The SensorEngine
    protected SensorEngine sensorEngine;
    protected Map<WaveRecipeAuthorization, IWaveRecipeOutputDataListener> listenerMap;
    
    protected void throwNotImplemented() {
        String className = getClass().getName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        throw new RuntimeException(className+"#"+methodName+" not implemented yet!");
    }
    
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        
        try {
            // get the SensorEngine
            SensorEngine.init(this);
            sensorEngine = SensorEngine.getInstance();
            listenerMap = new HashMap<WaveRecipeAuthorization, IWaveRecipeOutputDataListener>();
            
            /**
             * open up our sqlite database and restore the saved authorizations
             */
            databaseHelper = new RecipeDbHelper(this);
        
            // load the client auth map (and create the inverse map)
            clientKeyNameMap = databaseHelper.loadClientKeyNameMap();
            clientNameKeyMap = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : clientKeyNameMap.entrySet()) {
                clientNameKeyMap.put(entry.getValue(), entry.getKey());
            }
        
            authorizations = databaseHelper.loadAuthorizations(this); // the waveservice is passed so that we can load recipes
        } catch (Exception e) {
            Log.d(TAG, "Exception encountered in onCreate()", e);
        }
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
    
    @Override
    public void onDestroy() {
        // the databaseHelper may not be set up if the service doesn't fully start up
        if (databaseHelper != null) {
            databaseHelper.closeDatabase();
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
    public ArrayList<WaveRecipeAuthorization> recipeAuthorizations() {
        return authorizations;
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
     * get a recipe by its ID.  In this form, a recipe is delivered
     * immediately, or an exception is thrown
     */
    protected WaveRecipe getRecipeForId(String id)
            throws Exception {
        File f = recipeCacheFileForId(id);
        return WaveRecipe.createFromDisk(this, f.getPath());
    }
    
    /**
     * permitClientNameKeyPair
     */
    public synchronized boolean permitClientNameKeyPair(String packageName, String key) {
        // first, see if we have a stored key for that packageName
        if (clientKeyNameMap.containsValue(packageName)) {
            Log.d(TAG, "Checking key for previously registered package "+packageName);
            // see if the proposed key is correct
            if (clientKeyNameMap.containsKey(key) && clientKeyNameMap.get(key).equals(packageName)) {
                return true;
            } else {
                return false;
            }
        } else if (clientKeyNameMap.containsKey(key)) {
            Log.d(TAG, ""+packageName+" attempted use of another package's key. Removing key.");
            // we must make sure the secret key is not already in use. If a
            // conflict is detected, this means that a new client randomly
            // chose a key matching that of another client.  The safest thing
            // to do is to revoke the existing entry and fail the new one,
            // otherwise the new client would know it discovered a secret key.
            // another possibly would be to make the stored key a secure hash
            // of the client name and it's key, to avoid collisions of this
            // sort.
            if (!databaseHelper.removeClientKeyEntry(key)) {
                Log.w(TAG, "Failed to remove conflicting client key from database");
            }
            clientKeyNameMap.remove(key);
        } else if (databaseHelper.storeClientKeyNameEntry(key, packageName)) {
            Log.w(TAG, "Storing new key for "+packageName);
            clientKeyNameMap.put(key, packageName);
            return true;
        }
        return false;
    }
    
    /**
     * saveAuthorization
     * 
     * save an authorization.  Note that we don't check the validity of the
     * client key, because this is a private interface method.
     */
    public synchronized boolean saveAuthorization(WaveRecipeAuthorization auth) {
        try {
            if (databaseHelper.insertOrUpdateAuthorization(auth)) {
                authorizations.add(auth);
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, "Exception raised while saving authorization", e);
        }
        return false;
    }
    
    /**
     * getAuthorization
     * 
     * Used by the ViewRecipeAuthorizationActivity to call up an authorization
     * object
     */
    public synchronized WaveRecipeAuthorization getAuthorization(String recipeId, ComponentName clientName) {
        // first look for a full name match
        for (WaveRecipeAuthorization auth : authorizations) {
            if (auth.getRecipe().getId().equals(recipeId)) {
                if (clientName.equals(auth.getRecipeClientName())) {
                    return auth;
                }
            }
        }
        // then look for a package match
        return getAuthorization(recipeId, clientName.getPackageName());
    }
    
    /**
     * alternate getAuthorization matching on recipeId and client package name
     */
    protected WaveRecipeAuthorization getAuthorization(String recipeId, String clientPackageName) {
        for (WaveRecipeAuthorization auth : authorizations) {
            if (auth.getRecipe().getId().equals(recipeId)) {
                if (clientPackageName.equals(auth.getRecipeClientName().getPackageName())) {
                    return auth;
                }
            }
        }
        return null;
    }
    
    /**
     * resets the authorization system to a clean install state.  Database
     * info and caches are wiped.
     */
    protected synchronized void resetDatabase() {
        Log.d(TAG, "begin resetDatabase()");
        
        databaseHelper.emptyDatabase();
        
        clientKeyNameMap = new HashMap<String, String>();
        clientNameKeyMap = new HashMap<String, String>();
        authorizations = new ArrayList<WaveRecipeAuthorization>();
        
        Log.d(TAG, "end resetDatabase()");
    }
    
    /**
     * WAVESERVICE PUBLIC METHODS
     * 
     * @see IWaveServicePublic
     */
    private final IWaveServicePublic.Stub mPublicBinder = new IWaveServicePublic.Stub() {
        
        /**
         * NOTE: These methods have not been refactored, because it is an
         * apparent limit of AIDL such that we cannot call methods from
         * outside of this inner class (it might also be possible that we
         * cannot even call one method from another inside of this Stub()
         * implementation).
         */
        
        /**
         * isAuthorized
         */
        public boolean isAuthorized(String key, String recipeId) {
            // Log.d(TAG, "mPublicBinder.isAuthorized(\""+key+"\", \""+recipeId+"\")");
            
            // check the validity of the key
            if (clientKeyNameMap.containsKey(key)) {
                // recall the package name for the given key
                // this is a valid key
                String clientPackageName = clientKeyNameMap.get(key);
                
                for (WaveRecipeAuthorization auth : authorizations) {
                    if (auth.getRecipe().getId().equals(recipeId)) {
                        if (clientPackageName.equals(auth.getRecipeClientName().getPackageName())) {
                            if (auth.validForDate(new Date())) {
                                return true;
                            } else {
                                Log.d(TAG, "isAuthorized called for revoked recipe (for recipeId="+recipeId+")");
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "isAuthorized called using invalid key (for recipeId="+recipeId+")");
            }
            return false;
        }

        /**
         * retrieveAuthorization
         */
        public WaveRecipeAuthorizationInfo retrieveAuthorizationInfo(String key, String recipeId) {
            // Log.d(TAG, "mPublicBinder.retrieveAuthorizationInfo(\""+key+"\", \""+recipeId+"\")");

            // check the validity of the key
            if (clientKeyNameMap.containsKey(key)) {
                // recall the package name for the given key
                // this is a valid key
                String clientPackageName = clientKeyNameMap.get(key);

                for (WaveRecipeAuthorization auth : authorizations) {
                    if (auth.getRecipe().getId().equals(recipeId)) {
                        if (clientPackageName.equals(auth.getRecipeClientName().getPackageName())) {
                            if (auth.validForDate(new Date())) {
                                return auth.asInfo();
                            } else {
                                Log.d(TAG, "retrieveAuthorizationInfo called for revoked recipe (for recipeId="+recipeId+")");
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "retrieveAuthorizationInfo called using invalid key (for recipeId="+recipeId+")");
            }
            return null;
        }

        /**
         * getAuthorizationIntent
         *
         * TODO: avoid the use of the key, and determine the caller of the IPC dynamically
         */
        public Intent getAuthorizationIntent(String recipeId, String key) {
            // Log.d(TAG, "mPublicBinder.getAuthorizationIntent(\""+recipeId+"\", \""+key+"\")");

            Intent authIntent = new Intent(ACTION_REQUEST_RECIPE_AUTHORIZE);
            authIntent.putExtra(RECIPE_ID_EXTRA, recipeId);
            authIntent.putExtra(CLIENT_KEY_EXTRA, key);
            return authIntent;
        }

        /**
         * registerRecipeOutputListener
         */
        public boolean registerRecipeOutputListener(String key, String recipeId, IWaveRecipeOutputDataListener listener) {
            /**
             * this means we need to merge the authorization for this recipeId
             * and client into the scheduled sampling of sensors
             * we need to then make sure that the recipe is calculated at the
             * appropriate interval to fulfill this recipe
             * 
             * The current recipe format is such that we should call
             * ingestSensorData on it at the authorized rate (or lower).
             * It is up to the recipe to buffer and calculate. Possibly,
             * ingestSensorData should provide timestamped data to aid the
             * recipe.  The recipe reports output as necessary, which then
             * feeds through to the listener set here.  To keep the recipe
             * from blocking, we should invoke ingestSensorData from a
             * separate thread.  It should be up to the recipe to respond
             * appropriately.
             * 
             * So, what this means, is that we need to schedule the underlying
             * sensor at a rate sufficient for the recipe+authorizations that
             * require it, and make sure that ingestSensorData is called on
             * the appropriate recipe algorithm instance (one instance for
             * each authorization), invoked in separate threads.  We also need
             * to link the callbacks up to the listener here.  This method
             * more or less instantiates an instance of the authenticates the call to the sensor engine.
             */
            
            // check the validity of the key
            if (clientKeyNameMap.containsKey(key)) {
                // recall the package name for the given key
                // this is a valid key
                String clientPackageName = clientKeyNameMap.get(key);
                // look up the authorization
                for (WaveRecipeAuthorization auth : authorizations) {
                    if (auth.getRecipe().getId().equals(recipeId)) {
                        if (clientPackageName.equals(auth.getRecipeClientName().getPackageName())) {
                            if (auth.validForDate(new Date())) {
                                // authorization was found and is valid
                                // check if already registered
                                if (listenerMap.containsKey(auth)) {
                                    // listener already registered
                                    Log.d(TAG, "registerRecipeOutputListener called when already registered registered (for recipeId="+recipeId+")");
                                    return false;
                                } else {
                                    listenerMap.put(auth, listener);
                                    // Warning: passing WaveService.this could possibly mess with AIDL.  Not sure right now.
                                    boolean didSchedule = sensorEngine.scheduleAuthorization(auth, WaveService.this);
                                    if (!didSchedule) {
                                        listenerMap.remove(auth);
                                    }
                                    if (didSchedule) {
                                        Log.d(TAG, "registered output listener for recipeId="+recipeId);
                                    }
                                    return didSchedule;
                                }
                            } else {
                                Log.d(TAG, "registerRecipeOutputListener called for revoked recipe (for recipeId="+recipeId+")");
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "registerRecipeOutputListener called using invalid key (for recipeId="+recipeId+")");
            }
            return false;
        }

        /**
        * unregisterRecipeOutputListener
        */
        public boolean unregisterRecipeOutputListener(String key, String recipeId) {
            // check the validity of the key
            if (clientKeyNameMap.containsKey(key)) {
                // recall the package name for the given key
                // this is a valid key
                String clientPackageName = clientKeyNameMap.get(key);
                // look up the authorization
                for (WaveRecipeAuthorization auth : authorizations) {
                    if (auth.getRecipe().getId().equals(recipeId)) {
                        if (clientPackageName.equals(auth.getRecipeClientName().getPackageName())) {
                            if (auth.validForDate(new Date())) {
                                // authorization was found and is valid
                                if (listenerMap.containsKey(auth)) {
                                    listenerMap.remove(auth);
                                    Log.d(TAG, "unregistered output listener for recipeId="+recipeId);
                                    return true;
                                } else {
                                    Log.d(TAG, "unregisterRecipeOutputListener called when nothing was registered (for recipeId="+recipeId+")");
                                    return false;
                                }
                            } else {
                                Log.d(TAG, "unregisterRecipeOutputListener called for revoked recipe (for recipeId="+recipeId+")");
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "unregisterRecipeOutputListener called using invalid key (for recipeId="+recipeId+")");
            }
            return false;
        }
    };
    
    /**
     * WaveRecipeOutputListener methods
     */
    public void receiveDataForAuthorization(WaveRecipeOutputDataImpl data, WaveRecipeAuthorization authorization) {
        // forward the data through IPC to the listener
        IWaveRecipeOutputDataListener destination = listenerMap.get(authorization);
        // TODO: spawn a thread to write data to the listener
        try {
            destination.receiveWaveRecipeOutputData(data);
        } catch (RemoteException re) {
            Log.d(TAG, "RemoteException in receiveDataForAuthorization, connection to client must have been dropped.", re);
            boolean didUnschedule = sensorEngine.descheduleAuthorization(authorization);
            Log.d(TAG, "sensorEngine.descheduleAuthorization("+authorization+") => "+didUnschedule);
        }
    }
}