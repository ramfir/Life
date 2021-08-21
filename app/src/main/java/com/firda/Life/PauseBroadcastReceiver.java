package com.firda.Life;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Locale;

import static com.firda.Life.ExampleIntentService.TAG_JOB;
import static com.firda.Life.ExampleIntentService.TAG_POSITION;

public class PauseBroadcastReceiver extends BroadcastReceiver {

    public static final String PLAY_ACTION = "PLAY_ACTION";
    Gson gson = new Gson();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, ExampleIntentService.class);
        context.stopService(serviceIntent);

        int position = intent.getIntExtra(TAG_POSITION, 0);
        Job job = gson.fromJson(intent.getStringExtra(TAG_JOB), Job.class);

        String title = job.getTitle();//Job.jobs.get(position).getTitle();
        long length = job.getLength();// Job.jobs.get(position).getLength();
        int hour = (int) ((length / 3600) % 24);
        int min = (int) ((length / 60) % 60);
        int sec = (int) length % 60;
        StringBuilder timeLeftFormatted = new StringBuilder(String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hour, min, sec));

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        Intent actionStopBroRec = new Intent(context, StopBroadcastReceiver.class);
        PendingIntent penActionStopBroRec = PendingIntent.getBroadcast(context, 0,
                actionStopBroRec, 0);

        Intent actionPlayBroRec = new Intent(/*context, PlayBroadcastReceiver.class*/);
        actionPlayBroRec.putExtra(TAG_POSITION, position);
        actionPlayBroRec.putExtra(TAG_JOB, gson.toJson(job));

        actionPlayBroRec.setAction(PLAY_ACTION);
        PendingIntent penActionPlayBroRec = PendingIntent.getBroadcast(context, 0,
                actionPlayBroRec, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(timeLeftFormatted)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setVibrate(new long[] { 0, 1000, 1000, 0, 1000 })
                .setDefaults(Notification.DEFAULT_SOUND)
                .setProgress((int)job.getMaxProgress()/*Job.jobs.get(position).getMaxProgress()*/, (int)job.getProgr()/*Job.jobs.get(position).getProgr()*/, false)
                .setPriority(Notification.PRIORITY_LOW)
                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .addAction(R.drawable.ic_play_arrow_black_24dp, "Play", penActionPlayBroRec)
                .build();
        NotificationManager mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
}
