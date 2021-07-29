package com.firda.secondlife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SetupActivity extends AppCompatActivity {

    public static final String KEYJOBS = "Jobs";
    public static final String TAG = "SetupActivity";
    public static int jobsSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        jobsSize = getIntent().getExtras().getInt(KEYJOBS);
        Log.d(TAG, "onCreate: "+String.valueOf(jobsSize));

        JobFragment jobFragment = new JobFragment();
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, jobFragment)
                .addToBackStack(null)
                .commit();
    }
}
