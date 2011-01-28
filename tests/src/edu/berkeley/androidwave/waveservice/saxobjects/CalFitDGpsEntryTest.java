package edu.berkeley.androidwave.waveservice.saxobjects;

import junit.framework.Assert;
import java.io.InputStream;

/**
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.saxobjects.CalFitDGpsEntryTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */

public class CalFitDGpsEntryTest extends RailsXmlObjectTest {

    public void testToRailsXmlNoException() throws Throwable {
        //String fileXml = CalFitDGpsEntryTest.convertStreamToString(getInstrumentation().getContext().getAssets().open("test/CalFitDGpsEntry.xml"));
        
        CalFitDGpsEntry entry = new CalFitDGpsEntry();
        String entryXml = entry.toRailsXml();
        
        Assert.assertNotNull("xml is null", entryXml);
    }
    
    public void testBuildWithRailsXML() throws Throwable {
        InputStream is = getInstrumentation().getContext().getAssets().open("fixtures/railsxmlobjects/CalFitDGpsEntry.xml");
        
        CalFitDGpsEntry entry = CalFitDGpsEntry.buildWithRailsXml(is);
        
        Assert.assertNotNull("object is null", entry);
        // test some values we know to be in the XML
        Assert.assertEquals("accuracy incorrect", new Float(1.0), entry.accuracy);
        Assert.assertNull("bearing incorrect", entry.bearing);
        Assert.assertEquals("provider incorrect", "Some provider", entry.provider);
        Assert.assertEquals("subjectRoleId incorrect", new Integer(1), entry.subjectRoleId);
        Assert.assertNotNull("time is null", entry.time);
        Assert.assertEquals("year incorrect", (2010-1900), entry.time.getYear());
        String timeAsString = entry.time.toString();
        Assert.assertTrue("month/day incorrect", timeAsString.indexOf("Jun 16") > -1);
        Assert.assertEquals("hour incorrect", 17, entry.time.getHours());
        Assert.assertEquals("minute incorrect", 22, entry.time.getMinutes());
        Assert.assertEquals("second incorrect", 11, entry.time.getSeconds());
    }
}