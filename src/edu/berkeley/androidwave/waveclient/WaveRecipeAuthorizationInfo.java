// 
//  WaveRecipeAuthorizationInfo.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-28.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveclient;

import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;

/**
 * WaveRecipeAuthorization
 * 
 * Pacelable version of {@link edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization}
 */
public class WaveRecipeAuthorizationInfo implements Parcelable {
    
    public String recipeId;
    
    public Date authorizedDate;
    public Date modifiedDate;

    public WaveRecipeOutputDescription recipeOutputDescription;
    
    public double outputMaxRate;
    public double outputMaxPrecision;
    
    /**
     * Constructor
     */
    public WaveRecipeAuthorizationInfo(String id, Date authorizedDate, Date modifiedDate) {
        outputMaxRate = -1.0;
        outputMaxPrecision = -1.0;
        recipeId = id;
        this.authorizedDate = authorizedDate;
        this.modifiedDate = modifiedDate;
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
        if (!(o instanceof WaveRecipeAuthorizationInfo)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        WaveRecipeAuthorizationInfo lhs = (WaveRecipeAuthorizationInfo) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return recipeId.equals(lhs.recipeId) &&
            authorizedDate.equals(lhs.authorizedDate) &&
            modifiedDate.equals(lhs.modifiedDate) &&
            outputMaxRate == lhs.outputMaxRate &&
            outputMaxPrecision == lhs.outputMaxPrecision &&
            (recipeOutputDescription == null ? lhs.recipeOutputDescription == null
            : recipeOutputDescription.equals(lhs.recipeOutputDescription));
    }
    
    @Override public int hashCode() {
        long doubleFieldBits;
        
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + recipeId.hashCode();
        result = 31 * result + authorizedDate.hashCode();
        result = 31 * result + modifiedDate.hashCode();
        result = 31 * result + (recipeOutputDescription == null ? 0 : recipeOutputDescription.hashCode());

        doubleFieldBits = Double.doubleToLongBits(outputMaxRate);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));
        doubleFieldBits = Double.doubleToLongBits(outputMaxPrecision);
        result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));

        return result;
    }
    
    @Override public String toString() {
        return getClass().getName() + "[" +
            "recipeId=" + recipeId + ", " +
            "authorizedDate=" + authorizedDate + ", " +
            "modifiedDate=" + modifiedDate + ", " +
            "outputMaxRate=" + outputMaxRate + ", " +
            "outputMaxPrecision=" + outputMaxPrecision + ", " +
            "recipeOutputDescription=" + recipeOutputDescription +
            "]";
    }
    
    /**
     * Parcelable Methods
     */
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeLong(authorizedDate.getTime());
        dest.writeLong(modifiedDate.getTime());
        dest.writeParcelable(recipeOutputDescription, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeDouble(outputMaxRate);
        dest.writeDouble(outputMaxPrecision);
    }
    
    public static final Parcelable.Creator<WaveRecipeAuthorizationInfo> CREATOR = new Parcelable.Creator<WaveRecipeAuthorizationInfo>() {
        public WaveRecipeAuthorizationInfo createFromParcel(Parcel in) {
            return new WaveRecipeAuthorizationInfo(in);
        }
        
        public WaveRecipeAuthorizationInfo[] newArray(int size) {
            return new WaveRecipeAuthorizationInfo[size];
        }
    };
    
    private WaveRecipeAuthorizationInfo(Parcel in) {
        recipeId = in.readString();
        authorizedDate = new Date(in.readLong());
        modifiedDate = new Date(in.readLong());
        recipeOutputDescription = in.readParcelable(WaveRecipeOutputDescription.class.getClassLoader());
        outputMaxRate = in.readDouble();
        outputMaxPrecision = in.readDouble();
    }
}