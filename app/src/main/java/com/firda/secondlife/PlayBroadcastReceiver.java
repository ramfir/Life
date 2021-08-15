package com.firda.secondlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import static com.firda.secondlife.ExampleIntentService.TAG_JOB;
import static com.firda.secondlife.ExampleIntentService.TAG_POSITION;

public class PlayBroadcastReceiver extends BroadcastReceiver {

    Gson gson = new Gson();

    @Override
    public void onReceive(Context context, Intent intent) {
        int position = intent.getIntExtra(TAG_POSITION, 0);
        String jsonJob = intent.getStringExtra(TAG_JOB);

        Intent serviceIntent = new Intent(context, ExampleIntentService.class);
        serviceIntent.putExtra(TAG_POSITION, position);
        serviceIntent.putExtra(TAG_JOB, jsonJob);
        context.startService(serviceIntent);
    }
}
