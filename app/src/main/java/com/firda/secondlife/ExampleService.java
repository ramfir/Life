package com.firda.secondlife;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Locale;

import static com.firda.secondlife.App.CHANNEL_ID;
import static com.firda.secondlife.ExampleIntentService.TAG_ACTION;
import static com.firda.secondlife.ExampleIntentService.TAG_POSITION;
import static com.firda.secondlife.MainActivity.TAG;

public class ExampleService extends Service {

    Thread thread;
    boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int position = intent.getIntExtra(TAG_POSITION, 0);
        final String title = Job.jobs.get(position).getTitle();
        /*Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("innnn")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);*/

        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent actionStopBroRec = new Intent(this, StopBroadcastReceiver.class);
        final PendingIntent penActionStopBroRec = PendingIntent.getBroadcast(this, 0,
                actionStopBroRec, 0);

        Intent actionPauseBroRec = new Intent(this, PauseBroadcastReceiver.class);
        final PendingIntent penActionPauseBroRec = PendingIntent.getBroadcast(this, 0,
                actionPauseBroRec, 0);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!isRunning) {
                            startForeground(1, getNotification(
                                    title,
                                    Job.jobs.get(position).getLength(),
                                    pendingIntent,
                                    penActionStopBroRec,
                                    penActionPauseBroRec, position));
                            return;
                        }

                        Job.jobs.get(position).setLength(Job.jobs.get(position).getLength()-1000);
                        Job.jobs.get(position).setProgr(Job.jobs.get(position).getProgr()+1000);
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(TAG_ACTION);
                        sendBroadcast(broadcastIntent);
                        /*mNotificationManager.notify*/startForeground(1, getNotification(
                                title,
                                Job.jobs.get(position).getLength(),
                                pendingIntent,
                                penActionStopBroRec,
                                penActionPauseBroRec, position));
                        if (Job.jobs.get(position).getLength() <= 0 && !title.contains("finished")) {
                            Job.jobs.get(position).setTitle(Job.jobs.get(position).getTitle()+" | finished");
                            stopSelf();
                        }
                        else
                            handler.postDelayed(this, 1000);
                    }
                };
                handler.post(mRunnable);

            }
        };
        thread = new Thread(runnable);
        thread.start();
        isRunning = true;
        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    private Notification getNotification(String title, long content, PendingIntent pendingIntent,
                                         PendingIntent penActionStopBroRec, PendingIntent penActionPauseBroRec,
                                         int position) {
        int hour = (int) ((content / 3600000) % 24);
        int min = (int) ((content / 60000) % 60);
        int sec = (int) (content / 1000) % 60;
        StringBuilder timeLeftFormatted = new StringBuilder(String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hour, min, sec));
        return new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(timeLeftFormatted)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setVibrate(new long[] { 0, 1000, 1000, 0, 1000 })
                .setDefaults(Notification.DEFAULT_SOUND)

                .setProgress((int)Job.jobs.get(position).getMaxProgress(), (int)Job.jobs.get(position).getProgr(), false)

                .setPriority(Notification.PRIORITY_HIGH)

                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", penActionPauseBroRec)

                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
        Log.d(TAG, "onDestroy: neujeli");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
