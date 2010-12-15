package edu.berkeley.calfitwave.waveservice.saxobjects;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;
import android.util.Log;
import android.util.Xml;

public class CalFitDGpsEntry extends RailsXmlObject {
    // Contstants
    public static final String GPS_ENTRY_TAG = "cal-fit-d-gps-entry";
    
    // Attributes
    public Float accuracy;
    public Float altitude;
    public Float bearing;
    public String imei;
    public Float latitude;
    public Float longitude;
    public String provider;
    public Float speed;
    public Date time;
    // Associations
    public Integer subjectRoleId;
    
    
    public static CalFitDGpsEntry buildWithRailsXml(InputStream xmlInputStream) {
        CalFitDGpsEntry entry = new CalFitDGpsEntry();
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
            serializer.startTag("", GPS_ENTRY_TAG);
            RailsXmlObject.appendNamedFloatToSerializer("accuracy", accuracy, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("altitude", altitude, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("bearing", bearing, serializer);
            RailsXmlObject.appendNamedStringToSerializer("imei", imei, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("latitude", latitude, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("longitude", longitude, serializer);
            RailsXmlObject.appendNamedStringToSerializer("provider", provider, serializer);
            RailsXmlObject.appendNamedFloatToSerializer("speed", speed, serializer);
            RailsXmlObject.appendNamedDatetimeToSerializer("time", time, serializer);
            RailsXmlObject.appendNamedIntegerToSerializer("subject-role-id", subjectRoleId, serializer);
            serializer.endTag("", GPS_ENTRY_TAG);
            serializer.endDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}