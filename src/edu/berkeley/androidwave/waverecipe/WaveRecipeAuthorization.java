// 
//  WaveRecipeAuthorization.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-26.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waverecipe.granularitytable.GranularityTable;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import android.content.pm.Signature;
import android.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.*;

/**
 * WaveRecipeAuthorization
 * 
 * Links a recipe to a set of available local sensors, with rate and precision
 * information
 */
public class WaveRecipeAuthorization {
    
    private static final String TAG = WaveRecipeAuthorization.class.getSimpleName();
    
    protected WaveRecipe recipe;
    
    protected String recipeClientName;
    protected Signature[] recipeClientSignatures;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    public WaveRecipeAuthorization(WaveRecipe recipe) {
        this.recipe = recipe;

        sensorDescriptionMaxRateMap = new HashMap<WaveSensorDescription, Double>();
        sensorDescriptionMaxPrecisionMap = new HashMap<WaveSensorDescription, Double>();
    }
    
    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxRateMap() {
        return sensorDescriptionMaxRateMap;
    }
    
    public HashMap<WaveSensorDescription, Double> getSensorDescriptionMaxPrecisionMap() {
        return sensorDescriptionMaxPrecisionMap;
    }
    
    // Use @Override to avoid accidental overloading.
    @Override public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof WaveRecipeAuthorization)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveRecipeAuthorization lhs = (WaveRecipeAuthorization) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        // TODO: write .equals for WaveRecipe
        return recipe.getId().equals(lhs.recipe.getId()) &&
            recipeClientName.equals(lhs.recipeClientName) &&
            Arrays.equals(recipeClientSignatures, lhs.recipeClientSignatures) &&
            sensorDescriptionMaxRateMap.equals(lhs.sensorDescriptionMaxRateMap) &&
            sensorDescriptionMaxPrecisionMap.equals(lhs.sensorDescriptionMaxPrecisionMap);
    }
    
    @Override public int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Convert this authorization for use by wave clients.
     */
    public WaveRecipeAuthorizationInfo asInfo() {
        WaveRecipeAuthorizationInfo info = new WaveRecipeAuthorizationInfo(recipe.getId());
        
        info.recipeOutputDescription = recipe.getOutput();
        
        GranularityTable t = recipe.getGranularityTable();
        try {
            info.outputMaxRate = t.rateForSensorRates(sensorDescriptionMaxRateMap);
            info.outputMaxPrecision = t.precisionForSensorPrecisions(sensorDescriptionMaxPrecisionMap);
        } catch (Exception e) {
            Log.w(TAG, "Exception raised while calculating output rate and precision", e);
        }
        
        return info;
    }
    
    /**
     * produce a json archive of this authorization for recording into the
     * app's SQLite databases.
     */
    public String toJSONString() {
        return this.toJSON().toString();
    }
    
    protected JSONObject toJSON() {
        JSONObject asJson = new JSONObject();
        try {
            asJson.put("recipeId", recipe.getId());

            // clientName
            asJson.put("recipeClientName", recipeClientName);
            // clientSignatures
            JSONArray sigs = new JSONArray();
            for (int i=0; i<recipeClientSignatures.length; i++) {
                sigs.put(recipeClientSignatures[i].toCharsString());
            }
            asJson.put("recipeClientSignatures", sigs);
            // rateMap
            asJson.put("sensorDescriptionMaxRateMap", sensorDescriptionMaxRateMapAsJSON());
            // precesionMap
            asJson.put("sensorDescriptionMaxPrecisionMap", sensorDescriptionMaxPrecisionMapAsJSON());
        } catch (JSONException e) {
            Log.w(TAG, "Exception encountered while producing JSON from "+this, e);
        }
        return asJson;
    }
    
    public static WaveRecipeAuthorization fromJSONString(WaveRecipe recipe, String jsonString)
            throws Exception {
        
        JSONObject o = new JSONObject(jsonString);
        WaveRecipeAuthorization auth = null;
        try {
            String recipeId = o.getString("recipeId");
            if (!recipeId.equals(recipe.getId())) {
                throw new Exception("recipe does not match recipeId in jsonString");
            }
            auth = new WaveRecipeAuthorization(recipe);
            
            auth.recipeClientName = o.getString("recipeClientName");
            
            JSONArray a = o.getJSONArray("recipeClientSignatures");
            auth.recipeClientSignatures = new Signature[a.length()];
            for (int i=0; i<a.length(); i++) {
                auth.recipeClientSignatures[i] = new Signature(a.getString(i));
            }
            
            Iterator it;
            
            JSONObject rates = o.getJSONObject("sensorDescriptionMaxRateMap");
            it = rates.keys();
            while (it.hasNext()) {
                String k = (String)it.next();
                // get the rate double from the json
                Double d = (Double)(o.get(k));
                // the keys are references to sensordescriptions in the
                // recipe, so we must decode them
                WaveSensorDescription wsd = recipe.getSensorForInternalId(k);
                // store in in the map
                auth.sensorDescriptionMaxRateMap.put(wsd, d);
            }
            
            JSONObject precs = o.getJSONObject("sensorDescriptionMaxPrecisionMap");
            it = precs.keys();
            while (it.hasNext()) {
                String k = (String)it.next();
                // get the rate double from the json
                Double d = (Double)(o.get(k));
                // the keys are references to sensordescriptions in the
                // recipe, so we must decode them
                WaveSensorDescription wsd = recipe.getSensorForInternalId(k);
                // store in in the map
                auth.sensorDescriptionMaxRateMap.put(wsd, d);
            }
        } catch (JSONException e) {
            auth = null;
        }
        return auth;
    }
    
    /**
     * JSON helpers for referenced fields.  Would love to throw these in an
     * category, a la Objective-C, in another file for clarity.
     */
    protected JSONObject sensorDescriptionMaxRateMapAsJSON() {
        JSONObject o = new JSONObject();
        try {
            for (Entry<WaveSensorDescription, Double> entry : sensorDescriptionMaxRateMap.entrySet()) {
                WaveSensorDescription wsd = entry.getKey();
                Double d = entry.getValue();
                o.put(recipe.getInternalIdForSensor(wsd), d);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Exception encountered while producing JSON from "+sensorDescriptionMaxRateMap, e);
        }
        return o;
    }
    
    protected JSONObject sensorDescriptionMaxPrecisionMapAsJSON() {
        JSONObject o = new JSONObject();
        try {
            for (Entry<WaveSensorDescription, Double> entry : sensorDescriptionMaxPrecisionMap.entrySet()) {
                WaveSensorDescription wsd = entry.getKey();
                Double d = entry.getValue();
                o.put(recipe.getInternalIdForSensor(wsd), d);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Exception encountered while producing JSON from "+sensorDescriptionMaxPrecisionMap, e);
        }
        return o;
    }
}