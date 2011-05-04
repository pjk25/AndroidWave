// 
//  TableEntryTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-05-03.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import android.test.MoreAsserts;
import junit.framework.TestCase;

/**
 * TableEntryTest
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.granularitytable.TableEntryTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class TableEntryTest extends TestCase {
    
    public void testPreconditions() {
        TableEntry one = getFixtureOne();
        TableEntry two = getFixtureTwo();
        TableEntry likeOne = new TableEntry();
        likeOne.sensorAttributes.add(SensorAttributesTest.getFixtureOne());
        likeOne.outputRate = 1.0;
        likeOne.outputPrecision = 0.5;
        
        MoreAsserts.checkEqualsAndHashCodeMethods(one, one, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, likeOne, true);
        MoreAsserts.checkEqualsAndHashCodeMethods(one, two, false);
    }
    
    /**
     * FIXTURES
     */
    
    public static TableEntry getFixtureOne() {
        TableEntry e = new TableEntry();
        e.sensorAttributes.add(SensorAttributesTest.getFixtureOne());
        e.outputRate = 1.0;
        e.outputPrecision = 0.5;
        return e;
    }
    
    public static TableEntry getFixtureTwo() {
        TableEntry e = new TableEntry();
        e.sensorAttributes.add(SensorAttributesTest.getFixtureTwo());
        e.outputRate = 5.0;
        e.outputPrecision = 0.1;
        return e;
    }
}