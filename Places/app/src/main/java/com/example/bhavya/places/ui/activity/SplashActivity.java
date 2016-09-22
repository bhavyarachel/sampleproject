package com.example.bhavya.places.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bhavya.places.R;
import com.example.bhavya.places.sharedpreferences.LoggedInUserSharedPreference;

/**
 * Created by bhavya on 9/8/16.
 *
 * This is the launcher activity; appears when app icon is clicked.
 */

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;
    private Handler handler = new Handler();

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    };


    final Runnable runnableobj = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this, DrawerActivity.class);
            startActivity(i);
            finish();
        }



    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*check if email key in shared preferences is empty or not*/
        if (LoggedInUserSharedPreference.getUserName(getBaseContext()).length() == 0 ) {
            //for a new login
            handler.postDelayed(runnable, SPLASH_TIME_OUT);
        } else { //already logged in user
            handler.postDelayed(runnableobj, SPLASH_TIME_OUT);
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

}

