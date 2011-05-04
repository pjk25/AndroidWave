// 
//  WaveRecipeAuthorization.java
//  CalFitWaveProject
//  
//  Created by Philip Kuryloski on 2011-01-26.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waverecipe;

import edu.berkeley.androidwave.waveclient.WaveRecipeAuthorizationInfo;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waveservice.sensorengine.WaveSensor;

import android.content.ComponentName;
import android.content.pm.Signature;
import android.util.Log;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    protected Date revokedDate;
    protected Date modifiedDate;
    
    protected Set<SensorAttributes> sensorAttributes;
    
    public WaveRecipeAuthorization(WaveRecipe recipe) {
        this.recipe = recipe;
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
    
    public Signature[] getRecipeClientSignatures() {
        return recipeClientSignatures;
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
    
    public Date getRevokedDate() {
        return revokedDate;
    }
    
    public void setRevokedDate(Date d) {
        revokedDate = d;
    }
    
    public Date getModifiedDate() {
        return modifiedDate;
    }
    
    // TODO: automatically updated modified date when fields change
    public void setModifiedDate(Date d) {
        modifiedDate = d;
    }
    
    public Set<SensorAttributes> getSensorAttributes() {
        return sensorAttributes;
    }
    
    public void setSensorAttributes(Set<SensorAttributes> s) {
        sensorAttributes = s;
    }
    
    /**
     * getOutputRate
     * 
     * calculates the recipe output given the authorizations current
     * sensorAttributes and granularityTable
     */
    public double getOutputRate() throws Exception {
        GranularityTable t = recipe.getGranularityTable();
        return t.rateForSensorAttributes(getSensorAttributes());
    }
    
    public double getOutputPrecision() throws Exception {
        GranularityTable t = recipe.getGranularityTable();
        return t.precisionForSensorAttributes(getSensorAttributes());
    }
    
    /**
     * validForDate
     * 
     * An authorization is valid if it has an authorized date, that authorized
     * date is before the supplied date, and the revoked date is after the
     * supplied date (if a revoked date is not null)
     */
    public boolean validForDate(Date d) {
        if (authorizedDate == null) {
            return false;
        }
        if (d.before(authorizedDate)) {
            return false;
        }
        
        if (d.after(authorizedDate)) {
            if (revokedDate == null || d.before(revokedDate)) {
                return true;
            }
        }
        return false;
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
            (revokedDate == null ? lhs.revokedDate == null : revokedDate.equals(lhs.revokedDate)) &&
            modifiedDate.equals(lhs.modifiedDate) &&
            (sensorAttributes == null ? lhs.sensorAttributes == null : sensorAttributes.equals(lhs.sensorAttributes));
    }
    
    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + recipe.hashCode();

        result = 31 * result + (recipeClientName == null ? 0 : recipeClientName.hashCode());
        result = 31 * result + (recipeClientSignatures == null ? 0 : recipeClientSignatures.hashCode());

        result = 31 * result + (authorizedDate == null ? 0 : authorizedDate.hashCode());
        result = 31 * result + (revokedDate == null ? 0 : revokedDate.hashCode());
        result = 31 * result + modifiedDate.hashCode();

        result = 31 * result + (sensorAttributes == null ? 0 : sensorAttributes.hashCode());

        return result;
    }
    
    /**
     * Convert this authorization for use by wave clients.
     */
    public WaveRecipeAuthorizationInfo asInfo() {
        WaveRecipeAuthorizationInfo info = new WaveRecipeAuthorizationInfo(recipe.getId(), authorizedDate, modifiedDate); // revoked Date not supplied as clients won't recieve info for revoked authorizations
        
        info.recipeOutputDescription = recipe.getOutput();
        
        GranularityTable t = recipe.getGranularityTable();
        try {
            info.outputMaxRate = t.rateForSensorAttributes(sensorAttributes);
            info.outputMaxPrecision = t.precisionForSensorAttributes(sensorAttributes);
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
            // revokedDate
            if (revokedDate != null) {
                asJson.put("revokedDate", revokedDate.getTime());
            }
            // modifiedDate
            asJson.put("modifiedDate", modifiedDate.getTime());
            // sensorAttributes
            if (sensorAttributes != null) {
                asJson.put("sensorAttributes", sensorAttributesAsJSONArray());
            }
        } catch (JSONException e) {
            Log.w(TAG, "Exception encountered while producing JSON from "+this, e);
        }
        return asJson;
    }
    
    public static WaveRecipeAuthorization fromJSONString(WaveRecipe recipe, String jsonString)
            throws Exception {
        
        // TODO: handle these that fail, or update them
        JSONObject o = new JSONObject(jsonString);
        WaveRecipeAuthorization auth = null;
        try {
            String recipeId = o.getString("recipe_recipeId");
            if (!recipeId.equals(recipe.getId())) {
                throw new Exception("recipe does not match recipe_recipeId in jsonString");
            }
            auth = new WaveRecipeAuthorization(recipe);
            
            // restore the recipeClientName
            String packageName = o.getString("recipeClientName_packageName");
            String className = o.getString("recipeClientName_className");
            auth.recipeClientName = new ComponentName(packageName, className);
            
            // restore the recipeClientSignatures
            JSONArray a = o.getJSONArray("recipeClientSignatures");
            auth.recipeClientSignatures = new Signature[a.length()];
            for (int i=0; i<a.length(); i++) {
                auth.recipeClientSignatures[i] = new Signature(a.getString(i));
            }
            
            // restore the dates
            auth.authorizedDate = new Date(o.getLong("authorizedDate"));
            if (o.has("revokedDate")) {
                auth.revokedDate = new Date(o.getLong("revokedDate"));
            } else {
                auth.revokedDate = null;
            }
            auth.modifiedDate = new Date(o.getLong("modifiedDate"));
            
            // restore the sensorAttributes
            if (o.has("sensorAttributes")) {
                auth.sensorAttributes = new HashSet<SensorAttributes>();
                JSONArray saja = o.getJSONArray("sensorAttributes");
                for (int i=0; i<saja.length(); i++) {
                    JSONObject element = saja.getJSONObject(i);
                
                    SensorAttributes sa = new SensorAttributes();
                    String internalId = element.getString("sensorInternalId");
                    sa.sensorDescription = recipe.getSensorForInternalId(internalId);
                    sa.rate = element.getDouble("rate");
                    sa.precision = element.getDouble("precision");
                
                    auth.sensorAttributes.add(sa);
                }
            } else {
                auth.sensorAttributes = null;
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
    protected JSONArray sensorAttributesAsJSONArray() {
        JSONArray a = new JSONArray();
        try {
            for (SensorAttributes sa : sensorAttributes) {
                JSONObject o = new JSONObject();
                o.put("sensorInternalId", recipe.getInternalIdForSensor(sa.sensorDescription));
                o.put("rate", sa.rate);
                o.put("precision", sa.precision);
                a.put(o);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Exception encountered while producing JSON from "+sensorAttributes, e);
        }
        return a;
    }
}