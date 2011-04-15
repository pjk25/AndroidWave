// 
//  WaveRecipeOutputChannelTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-15.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
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
 * WaveRecipeOutputChannelTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveclient.WaveRecipeOutputChannelDescriptionTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeOutputChannelDescriptionTest extends AndroidTestCase {
    
    WaveRecipeOutputChannelDescription anOutputChannel;
    
    public void setUp() {
        anOutputChannel = new WaveRecipeOutputChannelDescription("magnitude");
    }
    
    public void testName() {
        assertEquals("magnitude", anOutputChannel.getName());
    }
    
    public void testEquals() {
        WaveRecipeOutputChannelDescription one = new WaveRecipeOutputChannelDescription("magnitude");
        WaveRecipeOutputChannelDescription two = new WaveRecipeOutputChannelDescription("magnitude");
        
        assertEquals(one, two);
        
        WaveRecipeOutputChannelDescription three = new WaveRecipeOutputChannelDescription("phase");
        
        MoreAsserts.assertNotEqual(one, three);
    }
    
    public void testParcelable() {
        Parcel p = Parcel.obtain();
        WaveRecipeOutputChannelDescription original = new WaveRecipeOutputChannelDescription("magnitude");
        
        original.writeToParcel(p, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        
        p.setDataPosition(0);
        WaveRecipeOutputChannelDescription restored = WaveRecipeOutputChannelDescription.CREATOR.createFromParcel(p);
        
        assertEquals(original, restored);
    }
}