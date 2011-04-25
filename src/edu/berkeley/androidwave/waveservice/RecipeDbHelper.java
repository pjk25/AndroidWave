// 
//  RecipeDbHelper.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-04-09.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.SQLException;
import android.util.Log;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RecipeDbHelper {
    
    private static final String TAG = "RecipeDbHelper";
    
    private static final String DATABASE_NAME = "androidwave_recipes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String RECIPE_CLIENT_KEYS_TABLE_NAME = "recipe_client_keys";
    private static final String RECIPE_AUTH_TABLE_NAME = "recipe_auth";
    
    protected SQLiteDatabase database;
    
    // TODO: add a signature to the column as well for added security against client name collisions
    static final class KeysColumns {
        public static final String _ID = "_id";
        // public static final String _COUNT = "_count";
        public static final String CLIENT_KEY = "client_key";
        public static final String CLIENT_NAME = "client_name";
        
        public static final String[] ALL = {CLIENT_KEY, CLIENT_NAME};
    }
    
    static final class AuthColumns {
        public static final String _ID = "_id";
        // public static final String _COUNT = "_count";
        public static final String RECIPE_ID = "recipe_id";
        public static final String SIGNATURE = "signature";
        public static final String AUTH_TS = "authorized";
        public static final String REVOKED_TS = "revoked";
        public static final String MODIFIED_TS = "modified";
        public static final String AUTH_INFO_DATA = "auth_info_data";
        
        public static final String[] ALL = {RECIPE_ID, SIGNATURE,
                                            AUTH_TS, REVOKED_TS, MODIFIED_TS,
                                            AUTH_INFO_DATA};
    }
    
    private DatabaseHelper mOpenHelper;
    
    protected RecipeDbHelper(Context c) {
        mOpenHelper = new DatabaseHelper(c);
        database = mOpenHelper.getWritableDatabase();
    }
    
    /**
     * This is the waveclient authorization key table, used to ensure that
     * an IPC call to the WaveService is from a specific and authorized client
     */
    protected synchronized Map<String, String> loadClientKeyNameMap() {
        Map<String, String> map = new HashMap<String, String>();

        Cursor c = database.query(RECIPE_CLIENT_KEYS_TABLE_NAME,
                                  KeysColumns.ALL,
                                  null,  null, null, null, null);
        if (c.moveToFirst()) {
            for (int i=0; i<c.getCount(); i++) {
                map.put(c.getString(0), c.getString(1));
                assert c.moveToNext() : c;
            }
        }
        
        return map;
    }
    
    protected synchronized boolean storeClientKeyNameEntry(String key, String name) {
        ContentValues cv = new ContentValues(2);
        cv.put(KeysColumns.CLIENT_KEY, key);
        cv.put(KeysColumns.CLIENT_NAME, name);
        long result = database.insert(RECIPE_CLIENT_KEYS_TABLE_NAME, null, cv);
        return result >= 0;
    }
    
    protected synchronized boolean removeClientKeyEntry(String key) {
        // null implementation
        return false;
    }
    
    
    protected WaveRecipeAuthorization[] loadAuthorized(WaveService waveService) {
        Log.d(TAG, "loadAuthorized(" + waveService + ")");
        // TODO: optimize query to select by timestamps
        Cursor c = database.query(RECIPE_AUTH_TABLE_NAME,
                                  AuthColumns.ALL,
                                  null, null, null, null, null);
        
        ArrayList<WaveRecipeAuthorization> authorized = new ArrayList<WaveRecipeAuthorization>();
        if (c.moveToFirst()) {
            for (int i=0; i<c.getCount(); i++) {
                Timestamp authTime = (c.isNull(2) ? null : Timestamp.valueOf(c.getString(2)));
                Timestamp revokedTime = (c.isNull(3) ? null : Timestamp.valueOf(c.getString(3)));
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (now.after(authTime)) {
                    if (revokedTime == null || revokedTime.after(now)) {
                        String recipeId = c.getString(0);
                        String authInfoData = c.getString(5);
                    
                        try {
                            WaveRecipe recipe = waveService.getRecipeForId(recipeId);
                            WaveRecipeAuthorization auth = WaveRecipeAuthorization.fromJSONString(recipe, authInfoData);
                            authorized.add(auth);
                        } catch (Exception e) {
                            Log.w(TAG, "Exception encountered while restoring WaveRecipeAuthorization from SQL database", e);
                        }
                    }
                }
                assert c.moveToNext() : c;
            }
        }
        
        return authorized.toArray(new WaveRecipeAuthorization[0]);
    }
    
    protected WaveRecipeAuthorization[] loadRevoked() {
        return null;
    }
    
    protected boolean saveAuthorization(WaveRecipeAuthorization auth) {
        // TODO: make sure the signature from this cert is enough to detect a change in the recipe package
        X509Certificate recipeCertificate = auth.getRecipe().getCertificate();
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        ContentValues cv = new ContentValues(AuthColumns.ALL.length);
        cv.put(AuthColumns.RECIPE_ID, auth.getRecipe().getId());
        cv.put(AuthColumns.SIGNATURE, recipeCertificate.getSignature());
        cv.put(AuthColumns.AUTH_TS, now.toString());
        cv.putNull(AuthColumns.REVOKED_TS);
        cv.put(AuthColumns.MODIFIED_TS, now.toString());
        cv.put(AuthColumns.AUTH_INFO_DATA, auth.toJSONString());
        
        long row;
        try {
            row = database.insertOrThrow(RECIPE_AUTH_TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.w(TAG, "Exception while saving authorization", e);
            return false;
        }
        
        return row >= 0;
    }
    
    protected boolean updateAuthorization(WaveRecipeAuthorization auth) {
        // null implementation
        return false;
    }
    
    protected boolean revokeAuthorization(WaveRecipeAuthorization auth) {
        // null implementation
        return false;
    }
    
    /**
     *
     * This class helps open, create, and upgrade the database file. Set to package visibility
     * for testing purposes.
     */
    static class DatabaseHelper extends SQLiteOpenHelper {
        
        DatabaseHelper(Context context) {
            // calls the super constructor, requesting the default cursor factory.
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
        *
        * Creates the underlying database with table name and column names taken from the
        * NotePad class.
        */
        @Override
        public void onCreate(SQLiteDatabase db) {
            
            db.execSQL("CREATE TABLE " + RECIPE_CLIENT_KEYS_TABLE_NAME + " ("
                    + KeysColumns._ID + " INTEGER PRIMARY KEY,"
                    + KeysColumns.CLIENT_KEY + " TEXT UNIQUE,"
                    + KeysColumns.CLIENT_NAME + " TEXT UNIQUE"
                    + ");");
            
            db.execSQL("CREATE TABLE " + RECIPE_AUTH_TABLE_NAME + " ("
                    + AuthColumns._ID + " INTEGER PRIMARY KEY,"
                    + AuthColumns.RECIPE_ID + " TEXT UNIQUE,"
                    + AuthColumns.SIGNATURE + " BLOB,"
                    + AuthColumns.AUTH_TS + " TEXT,"
                    + AuthColumns.REVOKED_TS + " TEXT,"
                    + AuthColumns.MODIFIED_TS + " TEXT,"
                    + AuthColumns.AUTH_INFO_DATA + " TEXT"
                    + ");");
        }

        /**
         *
         * Demonstrates that the provider must consider what happens when the
         * underlying datastore is changed. In this sample, the database is upgraded the database
         * by destroying the existing data.
         * A real application should upgrade the database in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           // Logs that the database is being upgraded
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");

           // Kills the table and existing data
           db.execSQL("DROP TABLE IF EXISTS "+RECIPE_CLIENT_KEYS_TABLE_NAME);
           db.execSQL("DROP TABLE IF EXISTS "+RECIPE_AUTH_TABLE_NAME);

           // Recreates the database with a new version
           onCreate(db);
        }
    }
    
    protected void closeDatabase() {
        database.close();
    }
}