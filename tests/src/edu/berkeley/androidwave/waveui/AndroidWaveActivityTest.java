package edu.berkeley.androidwave.waveui;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveui.AndroidWaveActivityTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class AndroidWaveActivityTest extends ActivityInstrumentationTestCase2<AndroidWaveActivity> {

    public AndroidWaveActivityTest() {
        super("edu.berkeley.androidwave.waveui", AndroidWaveActivity.class);
    }

    /**
     * Verifies that activity under test can be launched.
     */
    public void testActivityTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully", getActivity());
    }

}
