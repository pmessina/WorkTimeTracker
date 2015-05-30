package com.worktimetracker.worktimetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.worktimetracker.R;

/**
 * Show a Menu Screen before WorkTime Activity starts
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, WorkTimeActivity.class);
                startActivity(intent);
                finish();
            }

        }, 4000);
    }


}
