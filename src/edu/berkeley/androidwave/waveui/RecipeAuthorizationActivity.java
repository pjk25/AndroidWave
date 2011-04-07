// 
//  RecipeAuthorizationActivity.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.berkeley.androidwave.R;

/**
 * RecipeAuthorizationActivity
 * 
 * UI displayed when a client application requests authorization for a given
 * recipe
 * 
 * TODO: we now need to check if a recipe exists before populating (or
 * possibly even displaying) this UI. We might need to introduce a download
 * dialog, or possibly another activity
 */
public class RecipeAuthorizationActivity extends Activity {
    
    public static final String ACTION_DID_AUTHORIZE = "edu.berkeley.androidwave.intent.action.DID_AUTHORIZE";
    public static final String ACTION_DID_DENY = "edu.berkeley.androidwave.intent.action.DID_DENY";
    
    protected WaveRecipe theRecipe;
    
    // view refs
    TextView appName;
    TextView appSig;
    TextView recipeName;
    TextView recipeDescription;
    TextView recipeSig;
    Button authButton;
    Button denyButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleColor(Color.RED);
        setContentView(R.layout.recipe_auth);
        
        // connect to views
        appName = (TextView) findViewById(R.id.app_name);
        appSig = (TextView) findViewById(R.id.app_sig);
        recipeName = (TextView) findViewById(R.id.recipe_name);
        recipeDescription = (TextView) findViewById(R.id.recipe_description);
        recipeSig = (TextView) findViewById(R.id.recipe_sig);
        authButton = (Button) findViewById(R.id.auth_button);
        denyButton = (Button) findViewById(R.id.deny_button);
        
        authButton.setOnClickListener(mAuthListener);
        denyButton.setOnClickListener(mDenyListener);
        
        Intent i = getIntent();
        String recipeId = i.getStringExtra(WaveService.RECIPE_ID_EXTRA);
        String clientActivityName = null;
        if (getCallingActivity() != null) {
            clientActivityName = getCallingActivity().toString();
        }
        String clientPackage = getCallingPackage();
        
        try {
            theRecipe = WaveRecipe.createFromID(this, recipeId, 0);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "could not create recipe from id "+recipeId);
            theRecipe = null;
        }
        
        if (theRecipe != null) {
            if (clientActivityName != null) {
                appName.setText(clientActivityName);
            } else {
                appName.setText(clientPackage);
            }
            
            recipeName.setText(theRecipe.getName());
            recipeDescription.setText(theRecipe.getDescription());
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    
    private OnClickListener mAuthListener = new OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_OK, (new Intent()).setAction(ACTION_DID_AUTHORIZE));
            finish();
        }
    };
    
    private OnClickListener mDenyListener = new OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_OK, (new Intent()).setAction(ACTION_DID_DENY));
            finish();
        }
    };
}