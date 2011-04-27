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

import android.content.ComponentName;
import android.content.pm.Signature;
import android.util.Log;
import java.util.Arrays;
import java.util.Date;
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
    
    protected ComponentName recipeClientName;
    protected Signature[] recipeClientSignatures;
    
    protected Date authorizedDate;
    protected Date modifiedDate;
    
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxRateMap;
    protected HashMap<WaveSensorDescription, Double> sensorDescriptionMaxPrecisionMap;
    
    public WaveRecipeAuthorization(WaveRecipe recipe) {
        this.recipe = recipe;

        sensorDescriptionMaxRateMap = new HashMap<WaveSensorDescription, Double>();
        sensorDescriptionMaxPrecisionMap = new HashMap<WaveSensorDescription, Double>();
    }
    
    public WaveRecipe getRecipe() {
        return recipe;
    }
    
    public ComponentName getRecipeClientName() {
        return recipeClientName;
    }
    
    public void setRecipeClientName(ComponentName name) {
        recipeClientName = name;
    }
    
    public void setRecipeClientSignatures(Signature[] signatures) {
        recipeClientSignatures = signatures;
    }
    
    public Date getAuthorizedDate() {
        return authorizedDate;
    }
    
    public void setAuthorizedDate(Date d) {
        authorizedDate = d;
    }
    
    public Date getModifiedDate() {
        return modifiedDate;
    }
    
    // TODO: automatically updated modified date when fields change
    public void setModifiedDate(Date d) {
        modifiedDate = d;
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
            (authorizedDate == null ? lhs.authorizedDate == null : authorizedDate.equals(lhs.authorizedDate)) &&
            (modifiedDate == null ? lhs.modifiedDate == null : modifiedDate.equals(lhs.modifiedDate)) &&
            sensorDescriptionMaxRateMap.equals(lhs.sensorDescriptionMaxRateMap) &&
            sensorDescriptionMaxPrecisionMap.equals(lhs.sensorDescriptionMaxPrecisionMap);
    }
    
    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + recipe.hashCode();

        result = 31 * result + (recipeClientName == null ? 0 : recipeClientName.hashCode());
        result = 31 * result + (recipeClientSignatures == null ? 0 : recipeClientSignatures.hashCode());

        result = 31 * result + (authorizedDate == null ? 0 : authorizedDate.hashCode());
        result = 31 * result + (modifiedDate == null ? 0 : modifiedDate.hashCode());

        result = 31 * result + sensorDescriptionMaxRateMap.hashCode();
        result = 31 * result + sensorDescriptionMaxPrecisionMap.hashCode();

        return result;
    }
    
    /**
     * Convert this authorization for use by wave clients.
     */
    public WaveRecipeAuthorizationInfo asInfo() {
        WaveRecipeAuthorizationInfo info = new WaveRecipeAuthorizationInfo(recipe.getId(), authorizedDate, modifiedDate);
        
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
            // recipeId
            asJson.put("recipe_recipeId", recipe.getId());

            // clientName
            asJson.put("recipeClientName_packageName", recipeClientName.getPackageName());
            asJson.put("recipeClientName_className", recipeClientName.getClassName());
            // clientSignatures
            JSONArray sigs = new JSONArray();
            for (int i=0; i<recipeClientSignatures.length; i++) {
                sigs.put(recipeClientSignatures[i].toCharsString());
            }
            asJson.put("recipeClientSignatures", sigs);
            // authorizedDate
            asJson.put("authorizedDate", authorizedDate.getTime());
            // modifiedDate
            asJson.put("modifiedDate", modifiedDate.getTime());
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
            String recipeId = o.getString("recipe_recipeId");
            if (!recipeId.equals(recipe.getId())) {
                throw new Exception("recipe does not match recipe_recipeId in jsonString");
            }
            auth = new WaveRecipeAuthorization(recipe);
            
            String packageName = o.getString("recipeClientName_packageName");
            String className = o.getString("recipeClientName_className");
            auth.recipeClientName = new ComponentName(packageName, className);
            
            JSONArray a = o.getJSONArray("recipeClientSignatures");
            auth.recipeClientSignatures = new Signature[a.length()];
            for (int i=0; i<a.length(); i++) {
                auth.recipeClientSignatures[i] = new Signature(a.getString(i));
            }
            
            auth.authorizedDate = new Date(o.getLong("authorizedDate"));
            auth.modifiedDate = new Date(o.getLong("modifiedDate"));
            
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