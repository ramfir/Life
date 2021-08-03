package com.firda.secondlife;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static com.firda.secondlife.MainActivity.TAG;
import static com.firda.secondlife.MainActivity.TIMER_KEY;


// I'm using IntentService since its code runs on separate thread

public class ExampleIntentService extends IntentService {

    private PowerManager.WakeLock mWakeLock;
    private boolean serviceRunning = true;
    private NotificationManager mNotificationManager;
    public static final String TAG_ACTION = "MY_ACTION";
    public static final String TAG_POSITION = "POSITION";
    int position;

    public ExampleIntentService() {
        super("ExampleIntentService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Exampe:Wakelock");
        mWakeLock.acquire();

        mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        position = intent.getIntExtra(TAG_POSITION, 0);
        long length = Job.jobs.get(position).getLength();
        String title = Job.jobs.get(position).getTitle();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = getNotification(title, length, pendingIntent);
        startForeground(1, notification);

        for (long i = length; i >= 0 ; i-=1000) {
            if (!serviceRunning) break;

            Job.jobs.get(position).setLength(i);
            if (i == 0)
                Job.jobs.get(position).setTitle(Job.jobs.get(position).getTitle()+" | finished");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(TAG_ACTION);
            broadcastIntent.putExtra(TIMER_KEY, i);
            broadcastIntent.putExtra(TAG_POSITION, position);
            sendBroadcast(broadcastIntent);

            notification = getNotification(title, i, pendingIntent);
            mNotificationManager.notify(1, notification);

            SystemClock.sleep(1000);
        }

        stopSelf(); // without this method onDestoy() won't be called if Activity is destroyed
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
                .setVibrate(new long[] { 0, 1000, 1000, 0, 1000 })
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        serviceRunning = false;

        if (position < Job.jobs.size()-1) {
            position++;
            Intent serviceIntent = new Intent(this, ExampleIntentService.class);
            serviceIntent.putExtra(TAG_POSITION, position);
            startService(serviceIntent);
        }
        // mNotificationManager.cancelAll();
        //startForeground(1, null);

        /*MainActivity.works--;
        if (MainActivity.works > 0) {
            Intent serviceIntent = new Intent(this, ExampleIntentService.class);
            serviceIntent.putExtra("time", MainActivity.testLong);
            serviceIntent.putExtra("work", MainActivity.testString);

            startService(serviceIntent);
        }*/
    }
}
