package com.firda.secondlife;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

import static com.firda.secondlife.ExampleIntentService.TAG_POSITION;

public class PlayBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int position = intent.getIntExtra(TAG_POSITION, 0);
        Intent serviceIntent = new Intent(context, ExampleIntentService.class);
        serviceIntent.putExtra(TAG_POSITION, position);
        context.startService(serviceIntent);


        /*String title = Job.jobs.get(position).getTitle();
        long content = Job.jobs.get(position).getLength();
        int hour = (int) ((content / 3600000) % 24);
        int min = (int) ((content / 60000) % 60);
        int sec = (int) (content / 1000) % 60;
        StringBuilder timeLeftFormatted = new StringBuilder(String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hour, min, sec));
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        Intent actionStopBroRec = new Intent(context, StopBroadcastReceiver.class);
        PendingIntent penActionStopBroRec = PendingIntent.getBroadcast(context, 0,
                actionStopBroRec, 0);
        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(timeLeftFormatted)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setVibrate(new long[] { 0, 1000, 1000, 0, 1000 })
                .setDefaults(Notification.DEFAULT_SOUND)

                .setProgress((int)Job.jobs.get(position).maxProgress, (int)Job.jobs.get(position).progr, false)

                .setPriority(Notification.PRIORITY_HIGH)

                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .build();
        NotificationManager mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);*/
    }
}
