package edu.berkeley.calfitwave.waveui;

import android.app.Activity;
import android.os.Bundle;
import edu.berkeley.calfitwave.R;

/**
 * CalFitWaveActivity
 *
 * This is the primary launch Activity of the Wave UI, which should be
 * displayed when the user launches the Wave app on their phone. It should
 * bind to the WaveService over the private local binding.
 */
public class CalFitWaveActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
