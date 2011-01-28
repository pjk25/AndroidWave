package edu.berkeley.calfitwave.waveservice.saxobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.*;
import java.util.Date;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xmlpull.v1.XmlSerializer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

abstract class RailsXmlObject {
    public static RailsXmlObject buildWithRailsXml(InputStream xmlInputStream) {
        throw new RuntimeException("buildWithRailsXml is not implemented for RailsXmlObject");
    }
    
    public RailsXmlObject initWithRailsXml(InputStream xmlInputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(xmlInputStream);
        Element root = dom.getDocumentElement();
        this.assignInstanceVariablesFromChildNodes(root);
        return this;
    }
    
    public void assignInstanceVariablesFromChildNodes(Node n) throws Exception {
        NodeList items = n.getChildNodes();
        for (int i=0; i<items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)item;
                
                String javaName = toJavaCase(e.getTagName(), true);
                String type = e.getAttribute("type");
                boolean isNull = e.hasAttribute("nil") && (e.getAttribute("type") == "true");
                Field field = null;
                try {
                    field = this.getClass().getField(javaName);
                }
                catch(Exception exception) {
                    Log.d("CalFitD", "Ignoring tag "+javaName+" during XML deserialization");
                }
                
                if (field != null) {
                    if (!isNull) {
                        //String text = item.getTextContent(); // need higher api for this
                        item.normalize();
                        String text = null;
                        if (item.hasChildNodes()) {
                            text = item.getFirstChild().getNodeValue();
                        }
                        
                        Log.d("CalFitD", ""+e.getTagName()+" ("+type+")=> "+text);
                        if (text != null) {
                            text = text.trim();
                            try {
                                if (type.equalsIgnoreCase("float")) {
                                    field.set(this, new Float(text));
                                } else if (type.equalsIgnoreCase("integer")) {
                                    field.set(this, new Integer(text));
                                } else if (type.equalsIgnoreCase("datetime")) {
                                    SimpleDateFormat formatter = new SimpleDateFormat(
                                             "yyyy-MM-dd'T'hh:mm:ss'Z'");
                                    Date date = formatter.parse(text,new ParsePosition(0));
                                    field.set(this, date);
                                } else {
                                    field.set(this, text);
                                }
                            } catch(Exception exception) {
                                Log.d("CalFitD", "Couldn't convert "+text+" to an Object", exception);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * http://blog.codecentric.de/en/2009/02/stringutils-camelizestring/
     */
    private String toJavaCase(String value, boolean startWithLowerCase) {
    	String[] strings = value.toLowerCase().split("-");
    	for (int i = startWithLowerCase ? 1 : 0; i < strings.length; i++){
    	    String fl = strings[i].substring(0,1).toUpperCase();
    	    if (strings[i].length() > 1) {
    	        strings[i] = fl + strings[i].substring(1,strings[i].length());
    	    } else {
    	        strings[i] = fl;
    	    }
    	}
    	return TextUtils.join("", strings);
    }
    
    public abstract String toRailsXml();
    
    private static void appendNamedTypeToSerializer(String name, String type, String value, XmlSerializer s)
        throws Exception
    {
        s.startTag("", name);
        if (type != null) {
            s.attribute("", "type", type);
        }
        if (value != null) {
            s.text(value);
        } else {
            s.attribute("", "nil", "true");
            s.text("");  // <- used to create a tag that opens and closes but is empty in the Rails style
        }
        s.endTag("", name);
    }
    
    public static void appendNamedFloatToSerializer(String name, Float f, XmlSerializer serializer)
        throws Exception
    {
        String v = (f == null ? null : f.toString());
        RailsXmlObject.appendNamedTypeToSerializer(name, "float", v, serializer);
    }
    
    public static void appendNamedIntegerToSerializer(String name, Integer i, XmlSerializer serializer)
        throws Exception
    {
        String v = (i == null ? null : i.toString());
        RailsXmlObject.appendNamedTypeToSerializer(name, "integer", v, serializer);
    }
    
    public static void appendNamedDatetimeToSerializer(String name, Date d, XmlSerializer serializer)
        throws Exception
    {
        // probably need to format the Date
        String v = (d == null ? null : d.toString());
        RailsXmlObject.appendNamedTypeToSerializer(name, "datetime", v, serializer);
    }
    
    public static void appendNamedStringToSerializer(String name, String s, XmlSerializer serializer)
        throws Exception
    {
        RailsXmlObject.appendNamedTypeToSerializer(name, null, s, serializer);
    }
}