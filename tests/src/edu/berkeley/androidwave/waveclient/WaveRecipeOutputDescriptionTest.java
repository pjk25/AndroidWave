// 
//  WaveRecipeOutputDescriptionTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-15.
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

/**
 * WaveRecipeOutputDescriptionTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveclient.WaveRecipeOutputDescriptionTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputDescriptionTest extends AndroidTestCase {
    
    WaveRecipeOutputDescription anOutput;
    
    public void setUp() {
        anOutput = new WaveRecipeOutputDescription("AccelerometerMagnitude", "g");
    }
    
    public void testName() {
        assertEquals("AccelerometerMagnitude", anOutput.getName());
    }
    
    public void testUnits() {
        assertEquals("g", anOutput.getUnits());
    }
    
    public void testEquals() {
        WaveRecipeOutputDescription one = new WaveRecipeOutputDescription("AccelerometerMagnitude", "-m/s^2");
        WaveRecipeOutputDescription two = new WaveRecipeOutputDescription("AccelerometerMagnitude", "-m/s^2");
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, true);
        one.addChannel(new WaveRecipeOutputChannelDescription("magnitude"));
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
        two.addChannel(new WaveRecipeOutputChannelDescription("magnitude"));
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, true);
        
        WaveRecipeOutputDescription three = new WaveRecipeOutputDescription("Kcal", "kCal");
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, three, false);
    }
    
    public void testParcelable() {
        Parcel p = Parcel.obtain();
        WaveRecipeOutputDescription original = new WaveRecipeOutputDescription("AccelerometerMagnitude", "-m/s^2");
        original.addChannel(new WaveRecipeOutputChannelDescription("magnitude"));
        
        p.writeParcelable(original, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        
        p.setDataPosition(0);
        WaveRecipeOutputDescription restored = p.readParcelable(WaveRecipeOutputDescription.class.getClassLoader());
        p.recycle();
        
        assertEquals(original, restored);
    }
}