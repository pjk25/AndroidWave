// 
//  ContinuousGranularityTable.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-02-25.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe.granularitytable;

import edu.berkeley.androidwave.waverecipe.SpecifiesExpectedUnits;
import edu.berkeley.androidwave.waverecipe.WaveSensorDescription;

import android.util.Log;

import net.smplmathparser.EvaluationTree;
import net.smplmathparser.MathParser;
import net.smplmathparser.MathParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ContinuousGranularityTable extends GranularityTable {

    // all rates are in Hz
    protected String rateFormulaString;
    // precision is in fractions of the natural unit of the sensor
    protected String precisionFormulaString;
    
    protected HashMap<String, SpecifiesExpectedUnits> variableMap;
    
    public void setRateFormulaString(String s) {
        rateFormulaString = s;
    }
    
    public void setPrecisionFormulaString(String s) {
        precisionFormulaString = s;
    }
    
    public void setVariableMap(HashMap<String, SpecifiesExpectedUnits> map) {
        variableMap = map;
    }
    
    /**
     * mathHelper
     */
    protected double mathHelper(String formula, HashMap<SpecifiesExpectedUnits, Double> valueMap)
            throws Exception {
        String function = "y="+formula;
        MathParser parser = new MathParser();
        EvaluationTree tree = parser.parse(function);
        
        // Join our two maps for this particular evaluation
        HashMap<String, Double> variableToValueMap = new HashMap<String, Double>();
        for (Map.Entry<String, SpecifiesExpectedUnits> pair : variableMap.entrySet()) {
            variableToValueMap.put("#"+pair.getKey(), valueMap.get(pair.getValue()));
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
    public double rateForSensorRates(HashMap<SpecifiesExpectedUnits, Double> rateMap)
            throws Exception {
        
        double result;
        try {
            result = mathHelper(rateFormulaString, rateMap);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Hit exception while evaluating sensor rates...");
            Log.d(getClass().getSimpleName(), "\"" + rateFormulaString + "\"");
            Log.d(getClass().getSimpleName(), "" + variableMap);
            Log.d(getClass().getSimpleName(), "" + rateMap);
            throw e;
        }
        return result;
    }

    /**
     * precisionForSensorPrecisions
     */
    public double precisionForSensorPrecisions(HashMap<SpecifiesExpectedUnits, Double> precisionMap)
            throws Exception {
        
        double result;
        try {
            result = mathHelper(precisionFormulaString, precisionMap);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Hit exception while evaluating sensor precisions...");
            Log.d(getClass().getSimpleName(), "\"" + precisionFormulaString + "\"");
            Log.d(getClass().getSimpleName(), "" + variableMap);
            Log.d(getClass().getSimpleName(), "" + precisionMap);
            throw e;
        }
        return result;
    }
}