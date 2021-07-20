package com.firda.secondlife;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Locale;

public class ExampleIntentService extends IntentService {
    private static final String TAG = "ExampleIntentService";
    private PowerManager.WakeLock mWakeLock;
    public ExampleIntentService() {
        super("ExampleIntentService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Exampe:Wakelock");
        mWakeLock.acquire();
        Log.d(TAG, "WakeLock acquired");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent:");

        long input = intent.getLongExtra("time", 0);
        String work = intent.getStringExtra("work");

        long hour, min, sec;
        StringBuilder timeLeftFormatted;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        for (long i = input; i > 0 ; i-=1000) {
            hour = (i / 3600000) % 24;
            min = (i / 60000) % 60;
            sec = (i / 1000) % 60;
            timeLeftFormatted = new StringBuilder(String.format(Locale.getDefault(),
                                            "%02d:%02d:%02d", hour, min, sec));
            Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                    .setContentTitle(work)
                    .setContentText(timeLeftFormatted)
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                    .setVibrate(/*new long[]{ 0 }*/new long[] { 0, 1000, 1000, 0, 1000 })
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();

            startForeground(1, notification);
            SystemClock.sleep(1000);
        }




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mWakeLock.release();
        Log.d(TAG, "WakeLock released");
    }
}
