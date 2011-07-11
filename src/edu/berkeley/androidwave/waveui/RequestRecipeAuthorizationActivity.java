// 
//  RequestRecipeAuthorizationActivity.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 University of California, Berkeley. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.R;
import edu.berkeley.androidwave.waveexception.WaveRecipeNotCachedException;
import edu.berkeley.androidwave.waveclient.WaveRecipeOutputDescription;
import edu.berkeley.androidwave.waverecipe.granularitytable.*;
import edu.berkeley.androidwave.waverecipe.WaveRecipe;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waveservice.RecipeRetrievalResponder;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * RequestRecipeAuthorizationActivity
 * 
 * UI displayed when a client application requests authorization for a given
 * recipe
 * 
 * TODO: Handle download progress, failure and success updates from the service
 * TODO: More clearly define the recipe portion of the UI
 * TODO: Use dialogs instead of Toast where necessary file:///usr/local/android-sdk-mac_86/docs/guide/topics/ui/dialogs.html
 * TODO: This activity should not run if the app is already authorized
 * TODO: Somehow indicate to the user if this recipe is actually supported by this hardware
 */
public class RequestRecipeAuthorizationActivity extends Activity implements RecipeRetrievalResponder {
    
    private static final String TAG = RequestRecipeAuthorizationActivity.class.getSimpleName();
    
    public static final String ACTION_DID_AUTHORIZE = "edu.berkeley.androidwave.intent.action.DID_AUTHORIZE";
    public static final String ACTION_DID_DENY = "edu.berkeley.androidwave.intent.action.DID_DENY";
    
    protected WaveRecipe theRecipe;
    protected WaveService mService;
    protected boolean mBound = false;
    
    private boolean createDidSucceed;
    
    protected ComponentName recipeClientName;
    protected Signature[] recipeClientSignatures;
    protected WaveRecipeAuthorization recipeAuthorization;
    
    // view refs
    TextView appName;
    TextView appSig;
    TextView recipeName;
    TextView recipeDescription;
    TextView recipeSig;
    TextView ratePrecText;
    Button ratePrecButton;
    Button authButton;
    Button denyButton;
    
    protected ProgressDialog progressDialog;
    
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
        ratePrecText = (TextView) findViewById(R.id.rate_prec_text);
        ratePrecButton = (Button) findViewById(R.id.rate_prec_button);
        authButton = (Button) findViewById(R.id.auth_button);
        denyButton = (Button) findViewById(R.id.deny_button);
        
        // more UI setup
        ratePrecButton.setEnabled(false);
        ratePrecButton.setOnClickListener(mAdjustListener);
        authButton.setEnabled(false);
        authButton.setOnClickListener(mAuthListener);
        denyButton.setEnabled(false);
        denyButton.setOnClickListener(mDenyListener);
        
        // connect to WaveService
        createDidSucceed = true;
        Intent sIntent = new Intent(Intent.ACTION_MAIN);
        sIntent.setClass(this, WaveService.class);
        try {
            bindService(sIntent, mConnection, Context.BIND_AUTO_CREATE);
            // we have to wait for onCreate to finish before the binding happens
            
            recipeClientName = getCallingActivity();
            
            // get some name information about the requesting app
            PackageManager pm = getPackageManager();
            String callingActivityName = "From: Unknown application";
            try {
                ActivityInfo aInfo = pm.getActivityInfo(recipeClientName, 0);   // may need flag PackageManager.GET_META_DATA
                callingActivityName = "From: "+aInfo.loadLabel(pm);
            } catch (PackageManager.NameNotFoundException nnfe) {
                Log.d(getClass().getSimpleName(), "NameNotFoundException while getting info for calling activity", nnfe);
                createDidSucceed = false;
            }
            appName.setText(callingActivityName);
            
            // get some signature information about the requesting app
            String callingActivitySigString = "Signed: Unknown";
            try {
                PackageInfo pInfo = pm.getPackageInfo(getCallingPackage(), PackageManager.GET_SIGNATURES);
                recipeClientSignatures = pInfo.signatures;
                if (recipeClientSignatures != null && recipeClientSignatures.length > 0) {
                    String sigAscii = recipeClientSignatures[0].toCharsString();
                    callingActivitySigString = "Signed: "+sigAscii.substring(0,20)+"…";
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(getClass().getSimpleName(), "Exception while getting signature info for calling package", e);
                createDidSucceed = false;
            }
            appSig.setText(callingActivitySigString);
            
        } catch (Exception e) {
            Toast.makeText(RequestRecipeAuthorizationActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Exception during onCreate()", e);
            createDidSucceed = false;
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
        // check in with the service about the status of this recipe
        if (createDidSucceed) {
            Log.d(getClass().getSimpleName(), "RequestRecipeAuthorizationActivity has bound to "+mService);
            
            Intent i = getIntent();
            String recipeId = i.getStringExtra(WaveService.RECIPE_ID_EXTRA);
            
            // verify that authenticity of the requesting app
            String clientKey = i.getStringExtra(WaveService.CLIENT_KEY_EXTRA);
            if (mService.permitClientNameKeyPair(recipeClientName.getPackageName(), clientKey)) {
                theRecipe = null;
                File recipeCacheFile = mService.recipeCacheFileForId(recipeId);
                if (recipeCacheFile.exists()) {
                    afterRecipeCached(recipeCacheFile);
                } else {
                    // attempt to download the recipe
                    progressDialog = ProgressDialog.show(RequestRecipeAuthorizationActivity.this,
                                                         "", // title
                                                         "Downloading recipe...", // message
                                                         true, // indeterminate
                                                         true, // cancelable
                                                         new DialogInterface.OnCancelListener() {
                                                             public void onCancel(DialogInterface dialog) {
                                                                 setResult(RESULT_CANCELED);
                                                                 RequestRecipeAuthorizationActivity.this.finish();
                                                             }
                                                         });
                    mService.beginRetrieveRecipeForId(recipeId, this);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestRecipeAuthorizationActivity.this);
                builder.setMessage("Authentication failed for requesting package "+recipeClientName.getPackageName()+".\n\nIt has either been reset, re-installed, or is attempting to use another app's key.")
                       .setCancelable(false)
                       .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               setResult(RESULT_CANCELED);
                               RequestRecipeAuthorizationActivity.this.finish();
                           }
                       });
                AlertDialog alert = builder.show();
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    
    // TODO: consider making this argument a WaveRecipe object
    private void afterRecipeCached(File recipeCacheFile) {
        try {
            theRecipe = WaveRecipe.createFromDisk(this, recipeCacheFile);

            // Create an authorization object
            recipeAuthorization = new WaveRecipeAuthorization(theRecipe);
            recipeAuthorization.setRecipeClientName(recipeClientName);
            recipeAuthorization.setRecipeClientSignatures(recipeClientSignatures);

            // update the UI
            recipeName.setText("Recipe: "+theRecipe.getName());
            recipeDescription.setText(theRecipe.getDescription());
            String recipeSigner = theRecipe.getCertificate().getSubjectDN().toString();
            recipeSig.setText("Signed by: "+recipeSigner);

            ratePrecButton.setEnabled(true);
            authButton.setEnabled(true);
            denyButton.setEnabled(true);
        } catch (Exception e) {
            Log.d(TAG, "Exception creating recipe from "+recipeCacheFile+" in afterRecipeCached", e);
            boolean didDelete = recipeCacheFile.delete();
            Log.d(TAG, "\trecipeCacheFile.delete() => "+didDelete);
            
            // TODO: remove the bad recipe file (in fact it should not go right to the cache where it is)

            AlertDialog.Builder builder = new AlertDialog.Builder(RequestRecipeAuthorizationActivity.this);
            builder.setMessage("Error: Recipe retrieval failed (invalid recipe file)")
                   .setCancelable(false)
                   .setPositiveButton("Cancel Authorization", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           setResult(RESULT_CANCELED);
                           RequestRecipeAuthorizationActivity.this.finish();
                       }
                   });
            AlertDialog alert = builder.show();
        }
    }
    
    private void updateGranularityText() {
        WaveRecipeOutputDescription wrod = recipeAuthorization.getRecipe().getOutput();
        try {
            double outputRate = recipeAuthorization.getOutputRate();
            double outputPrecision = recipeAuthorization.getOutputPrecision();
            String message = String.format("Output will be generated at a rate of %fHz, in increments of %f%s", outputRate, outputPrecision, wrod.getUnits());
            
            ratePrecText.setText(message);
        } catch (Exception e) {
            Log.d(TAG, "Exception encountered while calculating recipe output granularity", e);
            ratePrecText.setText("Error encountered while calculating recipe output granularity.");
        }
    }
    
    private void chooseGranularity(boolean cascadeToAuth) {
        final boolean cascade = cascadeToAuth;
        GranularityTable t = recipeAuthorization.getRecipe().getGranularityTable();
        if (t instanceof DiscreetGranularityTable) {
            DiscreetGranularityTable dgt = (DiscreetGranularityTable) t;
            
            // get the recipe output to determine units
            WaveRecipeOutputDescription wrod = recipeAuthorization.getRecipe().getOutput();
            
            // create some labels for the user from the TableEntry(s)
            final List<TableEntry> tableEntries = dgt.getEntries();
            ArrayList<String> listItems = new ArrayList<String>(tableEntries.size());
            
            for (TableEntry te : tableEntries) {
                // TODO: for rates less than 1Hz, display in seconds or minutes per update
                listItems.add(String.format("%fHz, %f%s", te.outputRate, te.outputPrecision, wrod.getUnits()));
            }
            
            // now display a dialog of choices
            final CharSequence[] items = listItems.toArray(new String[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(RequestRecipeAuthorizationActivity.this);
            builder.setTitle("Select Recipe Granularity\n(Delivery rate, precision)");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    TableEntry te = tableEntries.get(item);
                    recipeAuthorization.setSensorAttributes(te.sensorAttributes);
                    // update the granularity text
                    updateGranularityText();
                    // extra visual for the user
                    Toast.makeText(getApplicationContext(), "Selected: "+items[item], Toast.LENGTH_SHORT).show();
                    
                    if (cascade) {
                        authButton.performClick();
                    }
                }
            });
            AlertDialog alert = builder.show();
        } else {
            Toast.makeText(RequestRecipeAuthorizationActivity.this, "Continuous granularity adjustments are not yet implemented.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    
    private OnClickListener mAdjustListener = new OnClickListener() {
        public void onClick(View v) {
            chooseGranularity(false);
        }
    };
    
    private OnClickListener mAuthListener = new OnClickListener() {
        public void onClick(View v) {
            if (recipeAuthorization.getSensorAttributes() == null) {
                // choose the granularity
                chooseGranularity(true);
            } else {
                // TODO: add confirmation dialog
            
                Date now = new Date();
                recipeAuthorization.setAuthorizedDate(now);
                recipeAuthorization.setModifiedDate(now);
        
                if (mService.saveAuthorization(recipeAuthorization)) {
                    setResult(RESULT_OK, (new Intent()).setAction(ACTION_DID_AUTHORIZE));
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestRecipeAuthorizationActivity.this);
                    builder.setMessage("AndroidWave: internal error.")
                           .setCancelable(false)
                           .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                    setResult(RESULT_CANCELED);
                                    RequestRecipeAuthorizationActivity.this.finish();
                               }
                           });
                    AlertDialog alert = builder.show();
                }
            }
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
            Log.d("RequestRecipeAuthorizationActivity.ServiceConnection", "onServiceConnected("+className+", "+service+")");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = ((WaveService.LocalBinder)service).getService();
            mBound = true;
            afterBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("RequestRecipeAuthorizationActivity.ServiceConnection", "onServiceDisconnected("+className+")");
            mBound = false;
        }
    };
    
    /**
     * RecipeRetrievalResponder implementation
     */
     public void handleRetrievalFailed(String recipeId, String message) {
         progressDialog.dismiss(); // dismiss so the dialog's onCancelled is not called
         
         AlertDialog.Builder builder = new AlertDialog.Builder(RequestRecipeAuthorizationActivity.this);
         builder.setMessage("Error downloading recipe:\n"+message)
                .setCancelable(false)
                .setPositiveButton("Cancel Authorization", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_CANCELED);
                        RequestRecipeAuthorizationActivity.this.finish();
                    }
                });
         AlertDialog alert = builder.show();
     }

     public void handleRetrievalFinished(String recipeId, File f) {
         progressDialog.dismiss(); // dismiss so the dialog's onCancelled is not called
         
         afterRecipeCached(f);
     }
}