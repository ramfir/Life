package com.firda.secondlife;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

public class ExampleService extends Service {

    Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       /* if (thread != null) thread.stop();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                new CountDownTimer(input, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        input = millisUntilFinished;
                        updateTextView(projectTime, millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        gitTime.setText("00:00:00");
                    }
                };
            }
        })*/
        long input = intent.getLongExtra("time", 0);
        String work = intent.getStringExtra("work");

        long hour = (input / 3600000) % 24;
        long min = (input / 60000) % 60;
        long sec = (input / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(work)
                .setContentText(timeLeftFormatted)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
