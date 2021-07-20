package com.firda.secondlife;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Locale;


// I'm using IntentService since its code runs on separate thread

public class ExampleIntentService extends IntentService {
    private static final String TAG = "ExampleIntentService";
    private PowerManager.WakeLock mWakeLock;
    private boolean serviceRunning = true;
    private NotificationManager mNotificationManager;

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

        mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //this.startForeground(1, null);
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
        Notification notification = getNotification(work, input, pendingIntent);
        startForeground(1, notification);
        for (long i = input; i > 0 ; i-=1000) {
            if (!serviceRunning) break;
            notification = getNotification(work, i, pendingIntent);
            /*hour = (i / 3600000) % 24;
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
                    .setVibrate(*//*new long[]{ 0 }*//*new long[] { 0, 1000, 1000, 0, 1000 })
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();*/

            //if (!serviceRunning) break;
            mNotificationManager.notify(1, notification); //startForeground(1, notification);

            SystemClock.sleep(1000);
        }
    }

    private Notification getNotification(String title, long content, PendingIntent pendingIntent) {
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
                .setVibrate(/*new long[]{ 0 }*/new long[] { 0, 1000, 1000, 0, 1000 })
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mWakeLock.release();
        Log.d(TAG, "WakeLock released");

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        serviceRunning = false;// mNotificationManager.cancelAll();
        //startForeground(1, null);
    }
}
