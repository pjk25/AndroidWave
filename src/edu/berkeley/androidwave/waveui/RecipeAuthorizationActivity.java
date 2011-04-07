// 
//  RecipeAuthorizationActivity.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.R;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

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
    protected WaveService mService;
    protected boolean mBound = false;
    
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
        
        // more UI setup
        authButton.setOnClickListener(mAuthListener);
        denyButton.setOnClickListener(mDenyListener);
        
        // connect to WaveService
        Intent sIntent = new Intent(Intent.ACTION_MAIN);
        sIntent.setClass(this, WaveService.class);
        try {
            bindService(sIntent, mConnection, Context.BIND_AUTO_CREATE);
            // we have to wait for onCreate to finish before the binding happens
        } catch (Exception e) {
            Toast.makeText(RecipeAuthorizationActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
            System.out.println("Encountered excepting during bindService in RecipeAuthorizationActivity with "+sIntent);
            e.printStackTrace();
        }
        
        // get some information about the requesting app
        String clientActivityName = null;
        if (getCallingActivity() != null) {
            clientActivityName = getCallingActivity().toString();
        }
        String clientPackage = getCallingPackage();

        if (clientActivityName != null) {
            appName.setText(clientActivityName);
        } else {
            appName.setText(clientPackage);
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    // TODO: modify this so it waits for recipe download, so that the ui comes
    // up.
    private void afterBind() {
        // check in with the service about the status of this recipe
        if (mBound) {
            Log.d(getClass().getSimpleName(), "RecipeAuthorizationActivity has bound to "+mService);
            
            Intent i = getIntent();
            String recipeId = i.getStringExtra(WaveService.RECIPE_ID_EXTRA);

            theRecipe = null;
            try {
                File recipeCacheFile = mService.recipeCacheFileForId(recipeId);
                if (recipeCacheFile != null) {
                    theRecipe = WaveRecipe.createFromDisk(this, recipeCacheFile.getPath());
                } else {
                    Toast.makeText(RecipeAuthorizationActivity.this, "Attempting to retrieve this recipe…", Toast.LENGTH_SHORT).show();
                    recipeCacheFile = mService.retrieveRecipeForID(recipeId);
                    if (recipeCacheFile != null) {
                        Toast.makeText(RecipeAuthorizationActivity.this, "Failure.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecipeAuthorizationActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        theRecipe = WaveRecipe.createFromDisk(this, recipeCacheFile.getPath());
                    }
                }
            } catch (Exception e) {
                Toast.makeText(RecipeAuthorizationActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            
            if (theRecipe == null) {
                //setResult(RESULT_CANCELED);
                //finish();
            } else {
                recipeName.setText(theRecipe.getName());
                recipeDescription.setText(theRecipe.getDescription());
            }
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
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.d("RecipeAuthorizationActivity.ServiceConnection", "onServiceConnected("+className+", "+service+")");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = ((WaveService.LocalBinder)service).getService();
            mBound = true;
            afterBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("RecipeAuthorizationActivity.ServiceConnection", "onServiceDisconnected("+className+")");
            mBound = false;
        }
    };
}