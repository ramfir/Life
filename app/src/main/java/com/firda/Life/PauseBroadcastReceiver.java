package com.firda.Life;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import static com.firda.Life.TimerService.TAG_JOB;
import static com.firda.Life.TimerService.TAG_POSITION;

public class PauseBroadcastReceiver extends BroadcastReceiver {

    public static final String PLAY_ACTION = "PLAY_ACTION";
    Gson gson = new Gson();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, TimerService.class);
        context.stopService(serviceIntent);

        int position = intent.getIntExtra(TAG_POSITION, 0);
        Type tasksListType = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(intent.getStringExtra(TAG_JOB), tasksListType);

        Task task = tasks.get(position); // gson.fromJson(intent.getStringExtra(TAG_JOB), Task.class);
        //Task task = gson.fromJson(intent.getStringExtra(TAG_JOB), Task.class);

        String title = task.getTitle();//Task.tasks.get(position).getTitle();
        long length = task.getDuration();// Task.tasks.get(position).getDuration();
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
        //actionPlayBroRec.putExtra(TAG_JOB, gson.toJson(task));
        actionPlayBroRec.putExtra(TAG_JOB, gson.toJson(tasks));

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
                .setProgress((int) task.getMaxProgress()/*Task.tasks.get(position).getMaxProgress()*/, (int) task
                        .getProgr()/*Task.tasks.get(position).getProgr()*/, false)
                .setPriority(Notification.PRIORITY_LOW)
                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .addAction(R.drawable.ic_play_arrow_black_24dp, "Play", penActionPlayBroRec)
                .build();
        NotificationManager mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
}
