// 
//  ContinuousGranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-25.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.SensorAttributes;
import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.util.Log;

import net.smplmathparser.EvaluationTree;
import net.smplmathparser.MathParser;
import net.smplmathparser.MathParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ContinuousGranularityTable extends GranularityTable {
    
    private static final String TAG = ContinuousGranularityTable.class.getSimpleName();

    // all rates are in Hz
    protected String rateFormulaString;
    // precision is in fractions of the natural unit of the sensor
    protected String precisionFormulaString;
    
    protected Map<String, WaveSensorDescription> variableMap;
    
    public void setRateFormulaString(String s) {
        rateFormulaString = s;
    }
    
    public void setPrecisionFormulaString(String s) {
        precisionFormulaString = s;
    }
    
    public void setVariableMap(Map<String, WaveSensorDescription> map) {
        variableMap = map;
    }
    
    /**
     * mathHelper
     */
    protected double mathHelper(String formula, Set<SensorAttributes> valueSet, boolean rate)
            throws Exception {
        String function = "y="+formula;
        MathParser parser = new MathParser();
        EvaluationTree tree = parser.parse(function);
        
        // Extract 
        HashMap<String, Double> variableToValueMap = new HashMap<String, Double>();
        for (Map.Entry<String, WaveSensorDescription> pair : variableMap.entrySet()) {
            Double attributeValue = null;
            for (SensorAttributes sa : valueSet) {
                if (pair.getValue().equals(sa.sensorDescription)) {
                    attributeValue = new Double( (rate ? sa.rate : sa.precision) );
                }
            }
            variableToValueMap.put("#"+pair.getKey(), attributeValue);
        }
        
        // set variables for evaluation
        for (Map.Entry<String, Double> pair : variableToValueMap.entrySet()) {
            tree.setVariable(pair.getKey(), pair.getValue().doubleValue());
        }
        
        return tree.evaluate();
    }
    
    /**
     * rateForSensorRates
     * 
     * Determines output rate for the recipe given a map of inputs and rates
     * associated with those inputs
     */
    @Override
    public double rateForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception {
        
        double result;
        try {
            result = mathHelper(rateFormulaString, attributes, true);
        } catch (Exception e) {
            Log.d(TAG, "Hit exception while evaluating sensor rates...", e);
            Log.d(TAG, "\"" + rateFormulaString + "\"");
            Log.d(TAG, "" + variableMap);
            Log.d(TAG, "" + attributes);
            throw e;
        }
        return result;
    }

    /**
     * precisionForSensorPrecisions
     */
    @Override
    public double precisionForSensorAttributes(Set<SensorAttributes> attributes)
            throws Exception {
        
        double result;
        try {
            result = mathHelper(precisionFormulaString, attributes, false);
        } catch (Exception e) {
            Log.d(TAG, "Hit exception while evaluating sensor precisions...", e);
            Log.d(TAG, "\"" + precisionFormulaString + "\"");
            Log.d(TAG, "" + variableMap);
            Log.d(TAG, "" + attributes);
            throw e;
        }
        return result;
    }
}