// 
//  RecipeDbHelper.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-04-09.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
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
    
    protected static final String DATABASE_NAME = "user_recipes.db";
    protected static final int DATABASE_VERSION = 1;
    protected static final String RECIPE_CLIENT_KEYS_TABLE_NAME = "recipe_client_keys";
    protected static final String RECIPE_AUTH_TABLE_NAME = "recipe_auth";
    
    protected SQLiteDatabase database;
    
    // TODO: keep track of revoked CLIENT_KEY/NAME pairs and reject their reuse
    
    // TODO: add a signature to the column as well for added security against client name collisions
    static final class KeysColumns {
        public static final String _ID = "_id";
        // public static final String _COUNT = "_count";
        public static final String CLIENT_KEY = "client_key";
        public static final String CLIENT_PKG = "client_pkg";
        
        public static final String[] ALL = {CLIENT_KEY, CLIENT_PKG};
    }
    
    static final class AuthColumns {
        public static final String _ID = "_id";
        // public static final String _COUNT = "_count";
        public static final String RECIPE_ID = "recipe_id";
        public static final String SIGNATURE = "signature";
        public static final String CLIENT_PKG = "client_pkg";
        public static final String AUTH_INFO_DATA = "auth_info_data";
        
        public static final String[] ALL = {RECIPE_ID, SIGNATURE,
                                            CLIENT_PKG, AUTH_INFO_DATA};
    }
    
    private DatabaseHelper mOpenHelper;
    
    protected RecipeDbHelper(Context c) {
        mOpenHelper = new DatabaseHelper(c);
        database = mOpenHelper.getWritableDatabase();
        
        // TOOD: clean the database of old (more than X days) revoked authorizations
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
                c.moveToNext();
            }
        }
        c.close();
        
        return map;
    }
    
    protected synchronized boolean storeClientKeyNameEntry(String key, String name) {
        ContentValues cv = new ContentValues(2);
        cv.put(KeysColumns.CLIENT_KEY, key);
        cv.put(KeysColumns.CLIENT_PKG, name);
        long result;
        try {
            result = database.insertOrThrow(RECIPE_CLIENT_KEYS_TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.w(TAG, "SQLException while storing "+cv, e);
            return false;
        }
        
        return result >= 0;
    }
    
    protected synchronized boolean removeClientKeyEntry(String key) {
        String[] whereArgs = { key };
        long count = database.delete(RECIPE_CLIENT_KEYS_TABLE_NAME, KeysColumns.CLIENT_KEY+"==?", whereArgs);
        assert count < 2 : key;
        return count == 1;
    }
    
    protected ArrayList<WaveRecipeAuthorization> loadAuthorizations(WaveService waveService) {
        Log.d(TAG, "loadAuthorizations(" + waveService + ")");
        
        Cursor c = database.query(RECIPE_AUTH_TABLE_NAME,
                                  AuthColumns.ALL,
                                  null, null, null, null, null);
        
        ArrayList<WaveRecipeAuthorization> authorized = new ArrayList<WaveRecipeAuthorization>(c.getCount());
        if (c.moveToFirst()) {
            for (int i=0; i<c.getCount(); i++) {
                String recipeId = c.getString(0);
                // TODO verify the signature column
                String authInfoData = c.getString(3);
                try {
                    WaveRecipe recipe = waveService.getRecipeForId(recipeId);
                    WaveRecipeAuthorization auth = WaveRecipeAuthorization.fromJSONString(recipe, authInfoData);
                    authorized.add(auth);
                } catch (Exception e) {
                    Log.w(TAG, "Exception encountered while restoring WaveRecipeAuthorization from SQL database", e);
                }
                c.moveToNext();
            }
        }
        c.close();
        
        return authorized;
    }
    
    protected boolean insertOrUpdateAuthorization(WaveRecipeAuthorization auth)
            throws Exception {
        
        String recipeId = auth.getRecipe().getId();
        String clientName = auth.getRecipeClientName().getPackageName();
        
        // first check if this authorization already exists in the database
        String selection = AuthColumns.RECIPE_ID + "=? AND " +
                           AuthColumns.CLIENT_PKG + "=?";
        String[] selectionArgs = { recipeId, clientName };
        Cursor c = database.query(RECIPE_AUTH_TABLE_NAME,
                                  new String[] { AuthColumns._ID },
                                  selection, selectionArgs,
                                  null, null, null);
        int count = c.getCount();
        c.close();
        boolean didSucceed = false;
        if (count == 0 || count == 1 ) {
            // TODO: make sure the signature from this cert is enough to detect a change in the recipe package
            X509Certificate recipeCertificate = auth.getRecipe().getCertificate();
            
            ContentValues cv = new ContentValues(AuthColumns.ALL.length);
            cv.put(AuthColumns.RECIPE_ID, auth.getRecipe().getId());
            cv.put(AuthColumns.SIGNATURE, recipeCertificate.getSignature());
            cv.put(AuthColumns.CLIENT_PKG, auth.getRecipeClientName().getPackageName());
            cv.put(AuthColumns.AUTH_INFO_DATA, auth.toJSONString());
            
            try {
                long row;
                if (count == 0) {
                    // not in db, we need to insert
                    row = database.insertOrThrow(RECIPE_AUTH_TABLE_NAME, null, cv);
                    didSucceed = (row >= 0);
                } else {
                    row = database.update(RECIPE_AUTH_TABLE_NAME, cv,
                                          selection, selectionArgs);
                    didSucceed = (row == 1);
                    if (row != 1) {
                        throw new Exception("Detected "+row+" rows in db for recipeId="+recipeId+" and clientName="+clientName);
                    }
                }
            } catch (SQLException se) {
                Log.w(TAG, "SQLException while saving authorization", se);
                return false;
            }
        } else {
            // matched 2 or more (or less than 0) rows, this is an exception
            throw new Exception("Detected "+count+" rows in db for recipeId="+recipeId+" and clientName="+clientName);
        }
        
        return didSucceed;
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
                    + KeysColumns.CLIENT_KEY + " TEXT UNIQUE NOT NULL,"
                    + KeysColumns.CLIENT_PKG + " TEXT UNIQUE NOT NULL"
                    + ");");
            
            db.execSQL("CREATE TABLE " + RECIPE_AUTH_TABLE_NAME + " ("
                    + AuthColumns._ID + " INTEGER PRIMARY KEY,"
                    + AuthColumns.RECIPE_ID + " TEXT NOT NULL,"
                    + AuthColumns.SIGNATURE + " BLOB,"
                    + AuthColumns.CLIENT_PKG + " TEXT NOT NULL,"
                    + AuthColumns.AUTH_INFO_DATA + " TEXT NOT NULL"
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
    
    protected synchronized void emptyDatabase() {
        long count = database.delete(RECIPE_CLIENT_KEYS_TABLE_NAME, "1", null);
        Log.w(TAG, "Deleted "+count+" record(s) from "+DATABASE_NAME+"/"+RECIPE_CLIENT_KEYS_TABLE_NAME);
        
        count = database.delete(RECIPE_AUTH_TABLE_NAME, "1", null);
        Log.w(TAG, "Deleted "+count+" record(s) from "+DATABASE_NAME+"/"+RECIPE_AUTH_TABLE_NAME);
    }
    
    protected void closeDatabase() {
        database.close();
    }
}