package edu.berkeley.androidwave.waveservice.saxobjects;

import junit.framework.Assert;
import java.io.InputStream;

/**
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waveservice.saxobjects.CalFitDKcalEntryTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */

public class CalFitDKcalEntryTest extends RailsXmlObjectTest {

    public void testToRailsXmlNoException() throws Throwable {
        //String fileXml = CalFitDKcalEntryTest.convertStreamToString(getInstrumentation().getContext().getAssets().open("test/CalFitDKcalEntry.xml"));
        
        CalFitDKcalEntry entry = new CalFitDKcalEntry();
        String entryXml = entry.toRailsXml();
        
        Assert.assertNotNull("xml is null", entryXml);
    }
    
    public void testBuildWithRailsXML() throws Throwable {
        InputStream is = getInstrumentation().getContext().getAssets().open("fixtures/railsxmlobjects/CalFitDKcalEntry.xml");
        
        CalFitDKcalEntry entry = CalFitDKcalEntry.buildWithRailsXml(is);
        
        Assert.assertNotNull("object is null", entry);
    }
}