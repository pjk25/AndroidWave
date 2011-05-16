// 
//  ParcelableWaveRecipeOutputDataTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-16.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 


package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.TestUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.HashMap;
import java.util.Map;

/**
 * ParcelableWaveRecipeOutputDataTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveclient.ParcelableWaveRecipeOutputDataTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class ParcelableWaveRecipeOutputDataTest extends AndroidTestCase {
    
    public void testPreconditions() {
        ParcelableWaveRecipeOutputData one = getFixtureOne();
        ParcelableWaveRecipeOutputData two = getFixtureTwo();
        
        Map<String, Double> outputValues = new HashMap<String, Double>();
        outputValues.put("magnitude", 0.22);
        ParcelableWaveRecipeOutputData likeOne = new ParcelableWaveRecipeOutputData(one.getTime(), outputValues);
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, one, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, likeOne, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
    }
    
    public void testParcelable() {
        Parcel p = Parcel.obtain();
        ParcelableWaveRecipeOutputData original = getFixtureOne();
        
        p.writeParcelable(original, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        
        p.setDataPosition(0);
        ParcelableWaveRecipeOutputData restored = p.readParcelable(ParcelableWaveRecipeOutputData.class.getClassLoader());
        p.recycle();
        
        assertEquals(original, restored);
    }
    
    /**
     * FIXTURES
     */
    
    public static ParcelableWaveRecipeOutputData getFixtureOne() {
        long time = System.currentTimeMillis();
        Map<String, Double> outputValues = new HashMap<String, Double>();
        outputValues.put("magnitude", 0.22);
        
        return new ParcelableWaveRecipeOutputData(time, outputValues);
    }
    
    public static ParcelableWaveRecipeOutputData getFixtureTwo() {
        long time = System.currentTimeMillis();
        Map<String, Double> outputValues = new HashMap<String, Double>();
        outputValues.put("phase", 1.2);
        
        return new ParcelableWaveRecipeOutputData(time, outputValues);
    }
}