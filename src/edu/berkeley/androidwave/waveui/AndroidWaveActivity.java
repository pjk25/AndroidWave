package edu.berkeley.androidwave.waveui;

import edu.berkeley.androidwave.R;
import edu.berkeley.androidwave.waveservice.WaveService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * AndroidWaveActivity
 *
 * This is the primary launch Activity of the Wave UI, which should be
 * displayed when the user launches the Wave app on their phone. It should
 * bind to the WaveService over the private local binding.
 */
public class AndroidWaveActivity extends Activity {

    protected WaveService mService;
    protected boolean mBound = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_activity_title);
        setContentView(R.layout.main);

        // connect to WaveService
        Intent sIntent = new Intent(Intent.ACTION_MAIN);
        sIntent.setClass(this, WaveService.class);
        try {
            bindService(sIntent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Toast.makeText(AndroidWaveActivity.this, "Exception encountered, see log.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            // setResult(RESULT_CANCELED);
            // finish();
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
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = ((WaveService.LocalBinder)service).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };
}
