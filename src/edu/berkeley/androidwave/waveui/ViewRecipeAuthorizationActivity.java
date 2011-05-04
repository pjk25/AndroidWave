// 
//  ViewRecipeAuthorizationActivity.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-04-28.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.R;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.*;
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
import java.util.Date;

/**
 * ViewRecipeAuthorizationActivity
 * 
 * Activity for viewing and revoking a WaveRecipeAuthorization.  Should
 * eventually allow for editing an Authorization as well.
 */
public class ViewRecipeAuthorizationActivity extends Activity {
    
    private static final String TAG = ViewRecipeAuthorizationActivity.class.getSimpleName();
    
    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String CLIENT_NAME_EXTRA = "client_name";
    
    protected WaveService mService;
    protected boolean mBound = false;
    
    protected String requestedRecipeId;
    protected ComponentName requestedClientName;
    
    protected WaveRecipeAuthorization authorization;
    
    // UI outlets
    TextView appNameTextView;
    TextView appSigTextView;
    TextView authDateTextView;
    TextView recipeNameTextView;
    TextView recipeDescriptionTextView;
    TextView recipeSigTextView;
    TextView ratePrecTextView;
    Button cancelButton;
    Button revokeButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_view);
        
        // connect UI outlets
        appNameTextView = (TextView) findViewById(R.id.app_name);
        appSigTextView = (TextView) findViewById(R.id.app_sig);
        authDateTextView = (TextView) findViewById(R.id.auth_date);
        recipeNameTextView = (TextView) findViewById(R.id.recipe_name);
        recipeDescriptionTextView = (TextView) findViewById(R.id.recipe_description);
        recipeSigTextView = (TextView) findViewById(R.id.recipe_sig);
        ratePrecTextView = (TextView) findViewById(R.id.rate_prec_text);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        revokeButton = (Button) findViewById(R.id.revoke_button);
        
        cancelButton.setOnClickListener(mCancelListener);
        revokeButton.setOnClickListener(mRevokeListener);
        
        ComponentName callingActivity = getCallingActivity();
        Intent i = getIntent();
        boolean shouldBind = true;
        if (i.hasExtra(RECIPE_ID_EXTRA)) {
            requestedRecipeId = i.getStringExtra(RECIPE_ID_EXTRA);
            
            if (callingActivity == null || callingActivity.equals(new ComponentName(this, AndroidWaveActivity.class))) {
                // started by the WaveUI, no need to auth
                if (i.hasExtra(CLIENT_NAME_EXTRA)) {
                    requestedClientName = (ComponentName) i.getParcelableExtra(CLIENT_NAME_EXTRA);
                } else {
                    shouldBind = false;
                    Log.d(TAG, "started without CLIENT_NAME intent extras");
                }
            } else {
                requestedClientName = callingActivity;
            }
        } else {
            shouldBind = false;
            Log.d(TAG, "started without RECIPE_ID intent extras");
        }
        
        if (shouldBind) {
            // connect to WaveService (Private Interface)
            Intent sIntent = new Intent(Intent.ACTION_MAIN);
            sIntent.setClass(this, WaveService.class);
            try {
                bindService(sIntent, mConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                Toast.makeText(ViewRecipeAuthorizationActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Exception during onCreate()", e);
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
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
    
    private void afterBind() {
        if (mBound) {
            Log.d(TAG, "ViewRecipeAuthorizationActivity has bound to "+mService);
            
            // we now need to call up the appropriate authorization
            // the recipeid and the client's ComponentName should be enough
            authorization = mService.getAuthorization(requestedRecipeId, requestedClientName);
            if (authorization != null) {
                // then we need to update the UI
                ComponentName recipeClientName = authorization.getRecipeClientName();
            
                PackageManager pm = getPackageManager();
                String appLabel = "Unknown";
                try {
                    ActivityInfo aInfo = pm.getActivityInfo(recipeClientName, 0);   // may need flag PackageManager.GET_META_DATA
                    appLabel = ""+aInfo.loadLabel(pm);
                } catch (PackageManager.NameNotFoundException nnfe) {
                    Log.d(TAG, "NameNotFoundException while getting info for calling activity", nnfe);
                }
                appNameTextView.setText("App: "+appLabel);
            
                String appSig = "";
                Signature[] sigs = authorization.getRecipeClientSignatures();
                if (sigs != null && sigs.length > 0) {
                    appSig = sigs[0].toCharsString();
                    if (appSig.length() > 28) {
                        appSig = appSig.substring(0, 28)+"…";
                    }
                }
                appSigTextView.setText("Signature : "+appSig);
                
                if (authorization.getRevokedDate() != null) {
                    authDateTextView.setTextColor(Color.RED);
                    authDateTextView.setText("Revoked on: "+authorization.getRevokedDate());
                } else {
                    authDateTextView.setText("Authorized on: "+authorization.getAuthorizedDate());
                }
                
                WaveRecipe recipe = authorization.getRecipe();
                recipeNameTextView.setText("Recipe: "+recipe.getName());
                recipeDescriptionTextView.setText(recipe.getDescription());
                String recipeSigner = recipe.getCertificate().getSubjectDN().toString();
                recipeSigTextView.setText("Signed by: "+recipeSigner);
                
                // TODO: set up table view
                try {
                    double outputRate = authorization.getOutputRate();
                    double outputPrecision = authorization.getOutputPrecision();
                    String message = String.format("Output will be generated at a rate of %fHz, in increments of %f%s", outputRate, outputPrecision, recipe.getOutput().getUnits());
                    
                    ratePrecTextView.setText(message);
                } catch (Exception e) {
                    Log.d(TAG, "Exception while getting recipe output granularity", e);
                    ratePrecTextView.setText("Error encountered while calculating recipe output granularity.");
                }
                
            } else {
                // TODO: use a dialog
                Log.w(TAG, "Could not find requested WaveRecipeAuthorization for id="+requestedRecipeId+", "+requestedClientName);
                Toast.makeText(ViewRecipeAuthorizationActivity.this, "Could not find the requested authorization", Toast.LENGTH_SHORT).show();
                
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
    
    private OnClickListener mRevokeListener = new OnClickListener() {
        public void onClick(View v) {
            Date now = new Date();
            authorization.setRevokedDate(now);
            authorization.setModifiedDate(now);
            
            if (mService.saveAuthorization(authorization)) {
                setResult(RESULT_OK);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewRecipeAuthorizationActivity.this);
                builder.setMessage("AndroidWave: internal error.")
                       .setCancelable(false)
                       .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               setResult(RESULT_CANCELED);
                               ViewRecipeAuthorizationActivity.this.finish();
                           }
                       });
                AlertDialog alert = builder.show();
            }
        }
    };
    
    private OnClickListener mCancelListener = new OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.d(TAG, "mConnection.onServiceConnected("+className+", "+service+")");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = ((WaveService.LocalBinder)service).getService();
            mBound = true;
            afterBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "mConnection.onServiceDisconnected("+className+")");
            mBound = false;
        }
    };
}