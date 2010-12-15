package edu.berkeley.calfitwave;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class waveui.CalFitWaveActivityTest \
 * edu.berkeley.calfitwave.tests/android.test.InstrumentationTestRunner
 */
public class CalFitWaveActivityTest extends ActivityInstrumentationTestCase2<CalFitWaveActivity> {

    public CalFitWaveActivityTest() {
        super("edu.berkeley.calfitwave", CalFitWaveActivity.class);
    }

}
