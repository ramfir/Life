package com.firda.secondlife;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Created this class because otherwise broadcast will not be recieved if app process is killed

public class StopBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, ExampleIntentService.class);
        context.stopService(serviceIntent);

        NotificationManager mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}
