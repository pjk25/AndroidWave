package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.R;
import edu.berkeley.androidwave.waverecipe.WaveRecipeAuthorization;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * AndroidWaveActivity
 *
 * This is the primary launch Activity of the Wave UI, which should be
 * displayed when the user launches the Wave app on their phone. It should
 * bind to the WaveService over the private local binding.
 */
public class AndroidWaveActivity extends ListActivity {
    
    private final String TAG = AndroidWaveActivity.class.getSimpleName();

    protected WaveService mService;
    protected boolean mBound = false;
    
    protected ArrayList<WaveRecipeAuthorization> authorizations = new ArrayList<WaveRecipeAuthorization>();
    protected AndroidWaveActivityListAdapter listAdapter;
    
    // UI outlets

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_activity_title);
        setContentView(R.layout.main);
        
        
        // connect the UI outlets

        // set up the ListAdapter
        listAdapter = new AndroidWaveActivityListAdapter(this, R.layout.main_authorization_cell, authorizations);
        setListAdapter(listAdapter);

        // connect to WaveService
        Intent sIntent = new Intent(Intent.ACTION_MAIN);
        sIntent.setClass(this, WaveService.class);
        try {
            bindService(sIntent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Toast.makeText(AndroidWaveActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Exception encountered in onCreate()", e);
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
    
    protected void afterBind() {
        Log.d(TAG, "afterBind()");
        // populate the list of authorizations
        authorizations = mService.recipeAuthorizations();
        Toast.makeText(AndroidWaveActivity.this, "Loaded "+authorizations.size()+" recipe(s)", Toast.LENGTH_SHORT).show();

        if (authorizations.size() > 0) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for (WaveRecipeAuthorization a : authorizations) {
                        listAdapter.add(a);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            };
            
            runOnUiThread(r);
        } else {
            Toast.makeText(AndroidWaveActivity.this, "You currently have not yet authorized any recipes.", Toast.LENGTH_SHORT).show();
            // TODO: set background text to "You currently have not yet authorized any recipes.  Any recipes that have been authorized will appear here."
        }
    }
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = ((WaveService.LocalBinder)service).getService();
            mBound = true;
            afterBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };
}
