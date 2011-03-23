// 
//  RecipeAuthorizationActivity.java
//  AndroidWaveProject
//  
//  Created by Philip Kuryloski on 2011-03-22.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave.waveui;

import android.app.Activity;
import android.os.Bundle;
import edu.berkeley.androidwave.R;

/**
 * RecipeAuthorizationActivity
 * 
 * UI displayed when a client application requests authorization for a given
 * recipe
 */
public class RecipeAuthorizationActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}