// 
//  RecipeDbHelperTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-04-26.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveservice;

import android.database.Cursor;
import android.database.sqlite.*;
import android.database.SQLException;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.Map;

/**
 * RecipeDbHelperTest
 * 
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.RecipeDbHelperTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class RecipeDbHelperTest extends AndroidTestCase {
    
    RecipeDbHelper databaseHelper;
    
    @Override
    public void setUp() {
        databaseHelper = new RecipeDbHelper(getContext());
        databaseHelper.emptyDatabase();
    }
    
    @Override
    public void tearDown() {
        databaseHelper.closeDatabase();
    }
    
    public void testClientKeyNameStorage() {
        // database is already empty
        Map<String, String> clientKeyNameMap = databaseHelper.loadClientKeyNameMap();
        assertNotNull("map from empty db should not be null", clientKeyNameMap);
        assertEquals("no entries in map from empty db", 0, clientKeyNameMap.size());
        
        String someKey = "sthochoacdoadaieiie";
        String someName = "edu.berkeley.waveapps.fitness";
        String otherKey = "rc,.bbetdaod,.cudiao";
        String otherName = "edu.berkeley.waveapps.pedometer";
        
        assertTrue("new pair should store", databaseHelper.storeClientKeyNameEntry(someKey, someName));
        // assertFalse("same pair should reject", databaseHelper.storeClientKeyNameEntry(someKey, someKey));
        // assertFalse("same key should reject", databaseHelper.storeClientKeyNameEntry(someKey, otherName));
        // assertFalse("same name should reject", databaseHelper.storeClientKeyNameEntry(otherKey, someName));
        // assertTrue("other new pair should store", databaseHelper.storeClientKeyNameEntry(otherKey, otherName));
        
        clientKeyNameMap = databaseHelper.loadClientKeyNameMap();
        assertNotNull("restored entries should not be null", clientKeyNameMap);
        // assertEquals("2 saved entries", 2, clientKeyNameMap.size());
        assertEquals("1 saved entries", 1, clientKeyNameMap.size());
        
        System.out.println(clientKeyNameMap.toString());
        
        assertTrue(clientKeyNameMap.containsKey(someKey));
        assertTrue(clientKeyNameMap.containsValue(someName));
        assertEquals(someName, clientKeyNameMap.get(someKey));
        
        // assertTrue(clientKeyNameMap.containsKey(otherKey));
        // assertTrue(clientKeyNameMap.containsValue(otherName));
        // assertEquals(otherName, clientKeyNameMap.get(otherKey));
    }
    
    public void testAuthStorage() {
        
    }
    
    public void testPreconditions() {
        String[] columns = { "_id" };

        Cursor c = databaseHelper.database.query(databaseHelper.RECIPE_CLIENT_KEYS_TABLE_NAME,
                                                 columns,
                                                 null, null,
                                                 null, null, null);
        assertEquals("0 rows in client keys table", 0, c.getCount());
        
        c = databaseHelper.database.query(databaseHelper.RECIPE_AUTH_TABLE_NAME,
                                          columns,
                                          null, null,
                                          null, null, null);
        assertEquals("0 rows in auth table", 0, c.getCount());
    }
}