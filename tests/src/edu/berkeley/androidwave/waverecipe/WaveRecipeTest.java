// 
//  WaveRecipeTest.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-01-27.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveexception.InvalidSignatureException;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waveservice.sensorengine.*;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.content.pm.*;
import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * WaveRecipeTest
 * 
 * WaveRecipe is the in memory representation of the transferable Recipe
 * object from the Recipe authority.
 * It should have - a unique identifier
 *                  a version
 *                  a granularity table
 *                  an algorithm representation
 *                  needed sensors
 *                  generated outputs
 * 
 * We use an InstrumentationTestCase so we can use fixtures from the test
 * app's assets
 * 
 * to run:
 * adb shell am instrument -w -e class edu.berkeley.androidwave.waverecipe.WaveRecipeTest edu.berkeley.androidwave.tests/android.test.InstrumentationTestRunner
 */
public class WaveRecipeTest extends InstrumentationTestCase {
    
    WaveRecipe recipeOne;
    File recipeOneFile;
    
    /**
     * copyAssetToInternal
     *
     * used for copying fixtures to app storage to simulate downloaded data
     * only tested with two component destination paths
     */
    private File copyAssetToInternal(String source, String dest)
        throws IOException {
        
        File targetFile = null;
        
        InputStream is = getInstrumentation().getContext().getAssets().open(source);
        
        Context targetContext = getInstrumentation().getTargetContext();
        String[] destComponents = dest.split("/", 2);
        OutputStream os = null;
        if (destComponents.length == 0) {
            // System.out.println("copyAssetToInternal -> creating "+dest);
            os = targetContext.openFileOutput(dest, Context.MODE_PRIVATE);
        } else {
            File dir = targetContext.getDir(destComponents[0], Context.MODE_PRIVATE);
            // System.out.println("copyAssetToInternal -> created "+dir);
            destComponents = dest.split("/");
            // System.out.println("copyAssetToInternal -> destComponents "+Arrays.toString(destComponents));
            targetFile = new File(dir, destComponents[destComponents.length-1]);
            if (targetFile.exists()) {
                System.out.print(this.getClass().getSimpleName() + ": copyAssetToInternal->Deleting existing file at "+targetFile+"...");
                if (targetFile.delete()) {
                    System.out.println(" done.");
                } else {
                    System.out.println(" fail.");
                }
            }
            // System.out.println("copyAssetToInternal -> targetFile = "+targetFile);
            os = new FileOutputStream(targetFile);
        }
        
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        is.close();
        os.close();
        
        return targetFile;
    }
    
    protected Date parseDateFromXmlString(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        Date d = formatter.parse(s,new ParsePosition(0));
        return d;
    }
    
    public void setUp() throws Exception {
        recipeOneFile = copyAssetToInternal("fixtures/waverecipes/one.waverecipe", "waverecipes/one.waverecipe");
    }
    
    /**
     * testRetrieveRecipe
     *
     * Retrieve a recipe from a recipe authority.  Note that we need a
     * running recipe server for this.
     */
    public void testCreateFromID() {
        fail("test not written yet");
        
        String recipeID = "";
        int version = 0;
        
        WaveRecipe testRecipe = WaveRecipe.createFromID(recipeID, version);
        
        //assertNotNull(testRecipe);
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
        recipeOne = WaveRecipe.createFromDisk(getInstrumentation().getContext(), recipeOneFile.getPath());
        
        // test the values in the recipeOne fixture
        assertEquals("getID should match that of recipe xml", "edu.berkeley.waverecipe.AccelerometerMagnitude", recipeOne.getID());
        
        Date versionDate = parseDateFromXmlString("2011-01-09 19:20:30.45-0800");
        assertEquals("getVersion should be the timestamp of the recipe's signature", versionDate, recipeOne.getVersion());
        
        assertEquals("check name", "Accelerometer Magnitude", recipeOne.getName());
        
        assertEquals("check description", "Measures intensity of motion of your device. Representative of your activity level.", recipeOne.getDescription());
        
        // test the complex fields of the WaveRecipe
        WaveSensor[] sensors = recipeOne.getSensors();
        assertEquals("recipeOne has one sensor", 1, sensors.length);
        WaveSensor theSensor = sensors[0];
        assertEquals("recipeOne's sensor is an accelerometer", WaveSensor.Type.ACCELEROMETER, theSensor.getType());
        assertTrue("sensor has units", theSensor.hasExpectedUnits());
        assertEquals("sensor unit is g", "g", theSensor.getExpectedUnits());
        
        WaveRecipeOutput[] recipeOutputs = recipeOne.getRecipeOutputs();
        assertEquals("recipeOne has one output", 1, recipeOutputs.length);
        WaveRecipeOutput theOutput = recipeOutputs[0];
        assertEquals("recipeOne's output name is ", "AccelerometerMagnitude", theOutput.getName());
        WaveRecipeOutputChannel[] outputChannels = theOutput.getChannels();
        assertEquals("AccelerometerMagnitude has one channel", 1, outputChannels.length);
        assertEquals("that channel is called \"magnitude\"", "magnitude", outputChannels[0].getName());
        assertEquals("that channel has units of g", "g", outputChannels[0].getUnits());
        
        GranularityTable table = recipeOne.getGranularityTable();
        assertNotNull(table);
        assertEquals("GranularityTable is continuous", ContinuousGranularityTable.class, table.getClass());
        
        // need to check mappings somehow, lets just try some values
        HashMap<SpecifiesExpectedUnits, Double> rateMap = new HashMap<SpecifiesExpectedUnits, Double>();
        rateMap.put(theSensor, 10.0);
        assertEquals("rate out equals rate in", 10.0, ((ContinuousGranularityTable)table).rateForSensorRates(rateMap));
        
        HashMap<SpecifiesExpectedUnits, Double> precisionMap = new HashMap<SpecifiesExpectedUnits, Double>();
        precisionMap.put(theSensor, 0.01);
        assertEquals("precision out equals precision in", 0.01, ((ContinuousGranularityTable)table).precisionForSensorPrecisions(precisionMap));
        
        // check that algorithmServiceName was assigned appropriately (by checking protected field)
        assertEquals("algorithmServiceName", "edu.berkeley.androidwave.waverecipesample.AccelerometerMagnitudeAlgorithm", recipeOne.algorithmServiceName);
    }
    
    /**
     * testRecipeOneAlgorithmService
     */
    public void testRecipeOneAlgorithmService()
            throws Exception {
        
        // build an instance from the fixture
        recipeOne = WaveRecipe.createFromDisk(getInstrumentation().getContext(), recipeOneFile.getPath());
        
        // check which packages are installed and stuff
        PackageManager pm = getInstrumentation().getContext().getPackageManager();
        PackageInfo pkgInfo = pm.getPackageInfo("edu.berkeley.androidwave.waverecipesample", PackageManager.GET_SERVICES);
        for (ServiceInfo sInfo : pkgInfo.services) {
            System.out.println(""+sInfo);
        }
        
        // trigger the bind
        recipeOne.bindAlgorithmService();
        
        // let the service start up.
        // maybe we should use a ServiceTestCase
        Thread.sleep(1000);
        
        assertNotNull("algorithmService should not be null after bind", recipeOne.getAlgorithmService());
        
        recipeOne.unbindAlgorithmService();
        
        assertNull("algorithmService should be null after unbind", recipeOne.getAlgorithmService());
    }
}