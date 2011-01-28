package edu.berkeley.androidwave.waveservice.saxobjects;

import junit.framework.Assert;
import android.test.InstrumentationTestCase;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * We use an InstrumentationTestCase so we can use fixtures from the test
 * app's assets
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.saxobjects.RailsXmlObjectTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */

public abstract class RailsXmlObjectTest extends InstrumentationTestCase {

    public abstract void testToRailsXmlNoException() throws Throwable;
    
    public abstract void testBuildWithRailsXML() throws Throwable;
    
    public static String convertStreamToString(InputStream is) throws Exception {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
      is.close();
      return sb.toString();
    }
}