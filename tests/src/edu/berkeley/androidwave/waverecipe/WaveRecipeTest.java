// 
//  WaveRecipeTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.TestUtils;
import edu.berkeley.androidwave.waveclient.WaveRecipeOutputDescription;
import edu.berkeley.androidwave.waveclient.WaveRecipeOutputChannelDescription;
import edu.berkeley.androidwave.waveexception.InvalidSignatureException;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waverecipe.waverecipealgorithm.WaveRecipeAlgorithm;
import edu.berkeley.androidwave.waveservice.sensorengine.*;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import java.io.*;
import java.security.cert.X509Certificate;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * WaveRecipeTest
 * 
 * WaveRecipe is the in memory representation of the transferable Recipe
 * object from the Recipe authority.
 * It should have - a unique identifier
 *                  a version
 *                  a certificate
 *                  a granularity table
 *                  an algorithm representation
 *                  needed sensors
 *                  generated outputs
 * 
 * TODO: add a facitily by which a recipe is noted as signed by a known recipe
 *       producer
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeTest extends AndroidTestCase {
    
    WaveRecipe recipeOne;
    
    protected Date parseDateFromXmlString(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        Date d = formatter.parse(s,new ParsePosition(0));
        return d;
    }
    
    /**
     * testPreconditions
     * 
     * same as testCreateFromDisk, but base functionality, so labeled
     * testPreconditions
     */
    public void testPreconditions()
            throws Exception {
        
        // build an instance from the fixture
        // first copy the fixture to the recipes cache
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        recipeOne = WaveRecipe.createFromDisk(getContext(), targetFile.getPath());
        
        // test the values in the recipeOne fixture
        assertEquals("getId should match that of recipe xml", "edu.berkeley.waverecipe.AccelerometerMagnitude", recipeOne.getId());
        
        Date versionDate = parseDateFromXmlString("2011-01-09 19:20:30.45-0800");
        assertEquals("getVersion should be the timestamp of the recipe's signature", versionDate, recipeOne.getVersion());
        
        assertEquals("check name", "Accelerometer Magnitude", recipeOne.getName());
        
        assertEquals("check description", "Measures intensity of motion of your device. Representative of your activity level.", recipeOne.getDescription());
        
        // check signature information
        X509Certificate cert = recipeOne.getCertificate();
        assertNotNull("has certificate", cert);
        assertEquals("cert DN", "CN=Android Debug, O=Android, C=US", cert.getSubjectDN().toString());
        
        // test the complex fields of the WaveRecipe
        WaveSensorDescription[] sensors = recipeOne.getSensors();
        assertEquals("recipeOne has one sensor", 1, sensors.length);
        WaveSensorDescription theSensor = sensors[0];
        assertEquals("recipeOne's sensor is an accelerometer", WaveSensorDescription.Type.ACCELEROMETER, theSensor.getType());
        assertTrue("sensor has units", theSensor.hasExpectedUnits());
        assertEquals("sensor unit is", "-m/s^2", theSensor.getExpectedUnits());
        
        WaveRecipeOutputDescription theOutput = recipeOne.getOutput();
        assertEquals("recipeOne's output name is ", "AccelerometerMagnitude", theOutput.getName());
        assertEquals("recipeOne's output has units of g", "g", theOutput.getUnits());
        WaveRecipeOutputChannelDescription[] outputChannels = theOutput.getChannels();
        assertEquals("AccelerometerMagnitude has one channel", 1, outputChannels.length);
        assertEquals("that channel is called \"magnitude\"", "magnitude", outputChannels[0].getName());
        
        GranularityTable table = recipeOne.getGranularityTable();
        assertNotNull(table);
        
        assertEquals("GranularityTable is discreet", DiscreetGranularityTable.class, table.getClass());
        
        DiscreetGranularityTable discreetTable = (DiscreetGranularityTable) table;
        assertEquals("GranularityTable has one rate entry", 1, discreetTable.getRateEntries().size());
        assertEquals("GranularityTable has one precision entry", 1, discreetTable.getPrecisionEntries().size());
        
        Map<WaveSensorDescription, Double> rateMap = null;
        for (Map<WaveSensorDescription, Double> m : discreetTable.getRateEntries().keySet()) {
            rateMap = m;
            // no need to break as this map should have size 1
        }
        
        assertNotNull(rateMap);
        assertEquals("input rate", 10.0, rateMap.get(theSensor).doubleValue());
        assertEquals("output rate", 10.0, discreetTable.getRateEntries().get(rateMap).doubleValue());
        
        Map<WaveSensorDescription, Double> precisionMap = null;;
        for (Map<WaveSensorDescription, Double> m : discreetTable.getPrecisionEntries().keySet()) {
            precisionMap = m;
            // no need to break as this map should have size 1
        }
        assertEquals("input precision (in m/s^2)", 0.010, precisionMap.get(theSensor).doubleValue());
        assertEquals("output precision (in g)", 0.001, discreetTable.getPrecisionEntries().get(precisionMap).doubleValue());
        
        /* Old Tests for continuous table
        assertEquals("GranularityTable is continuous", ContinuousGranularityTable.class, table.getClass());
        
        // need to check mappings somehow, lets just try some values
        HashMap<WaveSensorDescription, Double> rateMap = new HashMap<WaveSensorDescription, Double>();
        rateMap.put(theSensor, 10.0);
        assertEquals("rate out equals rate in", 10.0, ((ContinuousGranularityTable)table).rateForSensorRates(rateMap));
        
        HashMap<WaveSensorDescription, Double> precisionMap = new HashMap<WaveSensorDescription, Double>();
        precisionMap.put(theSensor, 0.01);
        assertEquals("precision out equals precision in", 0.01, ((ContinuousGranularityTable)table).precisionForSensorPrecisions(precisionMap));
         */
        
        // test the algorithm class
        assertNotNull("algorithmMainClass should not be null", recipeOne.algorithmMainClass);
        // make an instance
        Object algorithmInstanceAsObject = recipeOne.getAlgorithmInstance();
        assertNotNull("algorithmMainClass can be instantiated", algorithmInstanceAsObject);
        MoreAsserts.assertAssignableFrom(WaveRecipeAlgorithm.class, algorithmInstanceAsObject);
    }
    
    public void testToFromInternalId()
            throws Exception {
        File targetFile = TestUtils.copyTestAssetToInternal(getContext(), "fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
        recipeOne = WaveRecipe.createFromDisk(getContext(), targetFile.getPath());

        // try getting an id for a sensordescription not associated with this recipe
        WaveSensorDescription wrongDescription = new WaveSensorDescription(WaveSensorDescription.Type.MAGNETOMETER, "none");
        String id = recipeOne.getInternalIdForSensor(wrongDescription);
        assertNull("invalid sensor generates null id", id);

        // now get a valid id
        WaveSensorDescription wsd = recipeOne.getSensors()[0];
        id = recipeOne.getInternalIdForSensor(wsd);
        assertNotNull("valid sensor generates an id", id);
        
        // restore that sensor from the id we got
        WaveSensorDescription restored = recipeOne.getSensorForInternalId(id);
        assertNotNull("we should get a description from a valid id", restored);
        assertEquals(wsd, restored);
        
        // make sure a bogus id returns a null sensor
        restored = recipeOne.getSensorForInternalId("bogus");
        assertNull("a bogus id should return null", restored);
    }
}