// 
//  WaveRecipeAuthorizationInfoTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-02-04.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import edu.berkeley.androidwave.TestUtils;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.os.Parcel;
import android.os.Parcelable;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.util.HashMap;

/**
 * WaveRecipeAuthorizationTest
 * 
 * Unit test for the WaveRecipeAuthorizationInfo class
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfoTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeAuthorizationInfoTest extends AndroidTestCase {
    
    @SmallTest
    public void testEquals() throws Exception {
        WaveRecipeAuthorizationInfo one = new WaveRecipeAuthorizationInfo("edu.berkeley.myrecipe");
        one.outputMaxRate = 10.0;
        one.outputMaxPrecision = 0.01;
        one.recipeOutputDescription = new WaveRecipeOutputDescription("angle", "rad/s");
        one.recipeOutputDescription.addChannel(new WaveRecipeOutputChannelDescription("phi"));

        WaveRecipeAuthorizationInfo two = new WaveRecipeAuthorizationInfo("edu.berkeley.myrecipe");
        two.outputMaxRate = 10.0;
        two.outputMaxPrecision = 0.01;
        two.recipeOutputDescription = new WaveRecipeOutputDescription("angle", "rad/s");
        two.recipeOutputDescription.addChannel(new WaveRecipeOutputChannelDescription("phi"));
        
        assertEquals(one, two);
        
        WaveRecipeAuthorizationInfo three = new WaveRecipeAuthorizationInfo("edu.berkeley.myotherrecipe");
        three.outputMaxRate = 10.0;
        three.outputMaxPrecision = 0.01;
        three.recipeOutputDescription = new WaveRecipeOutputDescription("speed", "m/s");
        
        MoreAsserts.assertNotEqual(one, three);
    }
    
    @SmallTest
    public void testParcelable() throws Exception {
        Parcel p = Parcel.obtain();
        
        WaveRecipeAuthorizationInfo original = new WaveRecipeAuthorizationInfo("edu.berkeley.myrecipe");
        original.outputMaxRate = 10.0;
        original.outputMaxPrecision = 0.01;
        WaveRecipeOutputDescription od = new WaveRecipeOutputDescription("angle", "rad/s");
        od.addChannel(new WaveRecipeOutputChannelDescription("phi"));
        original.recipeOutputDescription = od;
        
        p.writeParcelable(original, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        
        p.setDataPosition(0);
        WaveRecipeAuthorizationInfo restored = p.readParcelable(WaveRecipeAuthorizationInfo.class.getClassLoader());
        p.recycle();
        
        assertEquals(original, restored);
    }
}