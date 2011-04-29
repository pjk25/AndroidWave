package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waveclient.IWaveServicePublic;
import edu.berkeley.androidwave.waveclient.IWaveRecipeOutputDataListener;
import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waveexception.WaveRecipeNotCachedException;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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
public class WaveService extends Service {
    
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
    
    protected void throwNotImplemented() {
        String className = getClass().getName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        throw new RuntimeException(className+"#"+methodName+" not implemented yet!");
    }
    
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        
        try {
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
        
        private WaveRecipeAuthorization retrieveAuthorization(String key, String recipeId) {
            // check the validity of the key
            if (clientKeyNameMap.containsKey(key)) {
                // recall the package name for the given key
                // this is a valid key
                String packageName = clientKeyNameMap.get(key);
                WaveRecipeAuthorization auth = getAuthorization(recipeId, packageName);
                if (auth.validForDate(new Date())) {
                    return auth;
                } else {
                    return null;
                }
            }
            // invalid key, return null
            Log.d(TAG, "retrieveAuthorization called using invalid key (for recipeId="+recipeId+")");
            return null;
        }

        /**
         * isAuthorized
         */
        public boolean isAuthorized(String key, String recipeId) {
            WaveRecipeAuthorization auth = retrieveAuthorization(key, recipeId);
            return (auth != null);
        }

        /**
         * retrieveAuthorization
         */
        public WaveRecipeAuthorizationInfo retrieveAuthorizationInfo(String key, String recipeId) {
            WaveRecipeAuthorization auth = retrieveAuthorization(key, recipeId);
            if (auth != null) {
                return auth.asInfo();
            }
            return null;
        }

        /**
         * getAuthorizationIntent
         *
         * TODO: avoid the use of the key, and determine the caller of the IPC dynamically
         */
        public Intent getAuthorizationIntent(String recipeId, String key) {
            Intent authIntent = new Intent(ACTION_REQUEST_RECIPE_AUTHORIZE);
            authIntent.putExtra(RECIPE_ID_EXTRA, recipeId);
            authIntent.putExtra(CLIENT_KEY_EXTRA, key);
            return authIntent;
        }

        /**
         * registerRecipeOutputListener
         */
        public boolean registerRecipeOutputListener(String key, String recipeId, IWaveRecipeOutputDataListener listener) {
            throwNotImplemented();
            return false;
        }

        /**
        * unregisterRecipeOutputListener
        */
        public boolean unregisterRecipeOutputListener(String key, String recipeId, IWaveRecipeOutputDataListener listener) {
            throwNotImplemented();
            return false;
        }
    };
}