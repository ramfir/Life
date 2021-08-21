package com.firda.Life;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Locale;


// I'm using IntentService since its code runs on separate thread

public class ExampleIntentService extends IntentService {

    public static final String TAG_ACTION = "MY_ACTION";
    public static final String TAG_POSITION = "POSITION";
    public static final String PAUSE_ACTION = "PAUSE_ACTION";
    public static final String TAG_JOB = "JOB";

    private PowerManager.WakeLock mWakeLock;
    private boolean serviceRunning;
    private NotificationManager mNotificationManager;

    private int position = -1;
    private Job job;
    Gson gson = new Gson();

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    private IBinder mIBinder = new MyBinder();

    public ExampleIntentService() {
        super("ExampleIntentService");
        setIntentRedelivery(true);
    }

    class MyBinder extends Binder {
        public ExampleIntentService getService() {
            return ExampleIntentService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Exampe:Wakelock");
        mWakeLock.acquire();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        position = intent.getIntExtra(TAG_POSITION, -1);
        String jobJson = intent.getStringExtra(TAG_JOB);
        job = gson.fromJson(jobJson, Job.class);

        if (position == -1)
            return;
        serviceRunning = true;

        mNotificationManager.cancelAll();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent actionStopBroRec = new Intent();
        actionStopBroRec.setAction(TAG_ACTION);
        PendingIntent penActionStopBroRec = PendingIntent.getBroadcast(this,
                                                                       0,
                                                                       actionStopBroRec,
                                                                       0);

        Intent actionPauseBroRec = new Intent();
        actionPauseBroRec.setAction(PAUSE_ACTION);
        actionPauseBroRec.putExtra(TAG_POSITION, position);
        actionPauseBroRec.putExtra(TAG_JOB, gson.toJson(job));
        PendingIntent penActionPauseBroRec = PendingIntent.getBroadcast(this,
                                                                        0,
                                                                        actionPauseBroRec,
                                                                        PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = getNotification(pendingIntent,
                                                    penActionStopBroRec,
                                                    penActionPauseBroRec);
        startForeground(1, notification);
        while (job.getLength() >= 0) {
            actionPauseBroRec.putExtra(TAG_JOB, gson.toJson(job));
            penActionPauseBroRec = PendingIntent.getBroadcast(this,
                                                              0,
                                                              actionPauseBroRec,
                                                              PendingIntent.FLAG_UPDATE_CURRENT);
            if (!serviceRunning)
                break;
            job.setProgr(job.getProgr() + 1);
            notification = getNotification(pendingIntent,
                                           penActionStopBroRec,
                                           penActionPauseBroRec);
            mNotificationManager.notify(1, notification);
            if (job.getLength() == 0 && !job.getTitle().contains("finished")) {
                mEditor.remove(job.getTitle()).apply();
                job.setTitle(job.getTitle() + " | finished");
                SystemClock.sleep(1000);
                serviceRunning = false;
                // without following method onDestoy() won't be called if Activity is destroyed
                stopSelf();
                mNotificationManager.notify(2, getNotification(pendingIntent, null, null));
                break;
            }
            job.setLength(job.getLength() - 1);
            SystemClock.sleep(1000);
        }
    }

    private Notification getNotification(PendingIntent pendingIntent,
                                         PendingIntent penActionStopBroRec,
                                         PendingIntent penActionPauseBroRec) {
        long length = job.getLength();
        String title = job.getTitle();
        int hour = (int) ((length / 3600) % 24);
        int min = (int) ((length / 60) % 60);
        int sec = (int) length % 60;
        StringBuilder timeLeftFormatted = new StringBuilder(String.format(Locale.getDefault(),
                                                                          "%02d:%02d:%02d",
                                                                          hour,
                                                                          min,
                                                                          sec));
        return new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(timeLeftFormatted)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setVibrate(new long[]{0, 1000, 1000, 0, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setProgress((int) job.getMaxProgress(), (int) job.getProgr(), false)
                .setPriority(Notification.PRIORITY_LOW)
                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", penActionPauseBroRec)
                .build();
    }

    public int getPosition() {
        return position;
    }

    public Job getJob() {
        return job;
    }

    public boolean isServiceRunning() {
        return serviceRunning;
    }

    private void doVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void saveJob() {
        if (job != null) {
            mEditor.putString(job.getTitle(), gson.toJson(job));
            mEditor.apply();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        serviceRunning = false;
        saveJob();
    }
}
