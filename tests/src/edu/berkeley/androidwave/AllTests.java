package edu.berkeley.androidwave;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite containing all tests for my application.
 * 
 * adb shell am instrument -w edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}