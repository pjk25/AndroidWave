package edu.berkeley.androidwave.waveservice.saxobjects;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;
import android.util.Log;
import android.util.Xml;

public class CalFitDKcalEntry extends RailsXmlObject {
    // Attributes
    public String imei;
    public Float kcal;
    public Float localH;
    public Float localV;
    public Date time;
    // Associations
    public Integer subjectRoleId;
    
    
    public static CalFitDKcalEntry buildWithRailsXml(InputStream xmlInputStream) {
        CalFitDKcalEntry entry = new CalFitDKcalEntry();
        try {
            entry.initWithRailsXml(xmlInputStream);
        } catch (Exception e) {
            Log.d("CalFitD", "Exception raised while building from XML", e);
            entry = null;
        }
        return entry;
    }
    
    public String toRailsXml() {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "cal-fit-d-kcal-entry");
            RailsXmlObject.appendNamedStringToSerializer("imei", imei, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("kcal", kcal, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("local-h", localH, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("local-v", localV, serializer);
            RailsXmlObject.appendNamedDatetimeToSerializer("time", time, serializer);
            RailsXmlObject.appendNamedIntegerToSerializer("subject-role-id", subjectRoleId, serializer);
            serializer.endTag("", "cal-fit-d-kcal-entry");
            serializer.endDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}