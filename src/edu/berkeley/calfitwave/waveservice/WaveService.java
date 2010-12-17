package edu.berkeley.calfitwave.waveservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

class WaveService extends Service {
    
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MAIN)) {
            return mPublicBinder;
        }
        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            return mUIBinder;
        }
        return null;
    }
    
    /**
     * The IWaveServicePublic is defined through IDL
     */
    private final IWaveServicePublic.Stub mPublicBinder = new IWaveServicePublic.Stub() {
        
    };
    
    /**
     * The UI service is a local binding only as there is only one UI app
     * It is identified by the ACTION_EDIT Intent
     */
    private final IBinder mUIBinder = new LocalBinder();
    
    /**
     * The associated local binder
     */
    public class LocalBinder extends Binder {
        WaveService getService() {
            return WaveService.this;
        }
    }
    
    /**
     * UI (LOCAL) METHODS
     */
    
}