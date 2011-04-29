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
    
    protected Map<String, String> clientKeyNameMap;
    protected ArrayList<WaveRecipeAuthorization> authorizations;
    protected Map<String, Set<WaveRecipeAuthorization> > validAuthorizationsByClientKey;
    protected Map<String, Set<WaveRecipeAuthorization> > revokedAuthorizationsByClientKey;
    
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
        
            clientKeyNameMap = databaseHelper.loadClientKeyNameMap();
            // invert the map for this portion
            HashMap<String, String> clientNameKeyMap = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : clientKeyNameMap.entrySet()) {
                clientNameKeyMap.put(entry.getValue(), entry.getKey());
            }
        
            authorizations = new ArrayList<WaveRecipeAuthorization>();
        
            ArrayList<WaveRecipeAuthorization> validAuthorizations = databaseHelper.loadAuthorized(this); // the waveservice is passed so that we can load recipes
            authorizations.addAll(validAuthorizations);
        
            // sort the authorizations by client name
            // maybe we should have a different table for each clientName?
            validAuthorizationsByClientKey = new HashMap<String, Set<WaveRecipeAuthorization> >();
            for (WaveRecipeAuthorization auth : validAuthorizations) {
                String clientName = auth.getRecipeClientName().getPackageName();
                if (clientNameKeyMap.containsKey(clientName)) {
                    String clientKey = clientNameKeyMap.get(clientName);
                    if (!validAuthorizationsByClientKey.containsKey(clientKey)) {
                        validAuthorizationsByClientKey.put(clientKey, new HashSet<WaveRecipeAuthorization>());
                    }
                    Set<WaveRecipeAuthorization> auths = validAuthorizationsByClientKey.get(clientKey);
                    auths.add(auth);
                } else {
                    Log.d(TAG, "Could not find key for recipe client with name "+clientName+" skipping stored auth "+auth);
                }
            }
            // for now we will ignore the revoked auths
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
     * save a new authorization.  Note that we don't check the validity of the
     * client key, because this is a private method.
     */
    public synchronized boolean saveAuthorization(String clientKey, WaveRecipeAuthorization auth) {
        if (databaseHelper.saveAuthorization(auth)) {
            authorizations.add(auth);
            
            if (!validAuthorizationsByClientKey.containsKey(clientKey)) {
                validAuthorizationsByClientKey.put(clientKey, new HashSet<WaveRecipeAuthorization>());
            }
            Set<WaveRecipeAuthorization> auths = validAuthorizationsByClientKey.get(clientKey);
            auths.add(auth);
            return true;
        }
        return false;
    }
    
    /**
     * updateAuthorization
     */
    public synchronized boolean updateAuthorization(WaveRecipeAuthorization auth) {
        // null implementation
        return false;
    }
    
    /**
     * getAuthorization
     * 
     * Used by the ViewRecipeAuthorizationActivity to call up an authorization
     * object
     */
    public synchronized WaveRecipeAuthorization getAuthorization(String recipeId, ComponentName clientName) {
        Date now = new Date();
        // first look for a full name match
        for (WaveRecipeAuthorization auth : authorizations) {
            if (auth.getRecipe().getId().equals(recipeId)) {
                if (clientName.equals(auth.getRecipeClientName())) {
                    return auth;
                }
            }
        }
        // TODO: then just a package match
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
        authorizations = new ArrayList<WaveRecipeAuthorization>();
        validAuthorizationsByClientKey = new HashMap<String, Set<WaveRecipeAuthorization> >();
        revokedAuthorizationsByClientKey = new HashMap<String, Set<WaveRecipeAuthorization> >();
        
        Log.d(TAG, "end resetDatabase()");
    }
    
    /**
     * WAVESERVICE PUBLIC METHODS
     * 
     * @see IWaveServicePublic
     */
     private final IWaveServicePublic.Stub mPublicBinder = new IWaveServicePublic.Stub() {
         
         private WaveRecipeAuthorization retrieveAuthorization(String key, String recipeId) {
             if (key != null && validAuthorizationsByClientKey.containsKey(key)) {
                // we recognize the client
                // now search for the specific auth
                Set<WaveRecipeAuthorization> auths = validAuthorizationsByClientKey.get(key);
                for (WaveRecipeAuthorization anAuth : auths) {
                    if (recipeId.equals(anAuth.getRecipe().getId())) {
                        return anAuth;
                    }
                }
             }
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