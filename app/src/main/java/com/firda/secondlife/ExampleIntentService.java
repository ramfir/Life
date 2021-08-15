package com.firda.secondlife;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Locale;

import static com.firda.secondlife.MainActivity.TAG;
import static com.firda.secondlife.MainActivity.TIMER_KEY;


// I'm using IntentService since its code runs on separate thread

public class ExampleIntentService extends IntentService {

    private PowerManager.WakeLock mWakeLock;
    private boolean serviceRunning;
    private NotificationManager mNotificationManager;
    public static final String TAG_ACTION = "MY_ACTIONn"; // added extra n bacause of productionsecondlife app
    public static final String TAG_POSITION = "POSITION";
    public static final String TAG_LENGTH = "LENGTH";
    public static final String TAG_TITLE = "TITLE";
    public static final String TAG_PROGRESS = "PROGRESS";
    public static final String TAG_MAXPROGRESS = "MAXPROGRESS";
    public static final String PAUSE_ACTION = "PAUSE_ACTION";
    public static final String TAG_JOB = "JOB";

    int position = -1;
    /*long length;
    String title;
    long progr;
    long maxProgress;*/
    Job job;
    Gson gson = new Gson();

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public int getPosition() {return position;}
    /*public long getLength() {return length;}
    public long getProgr() {return progr;}
    public String getTitle() {return title;}*/
    public Job getJob() { return job; }
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
        mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "onCreate: service");

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        position = intent.getIntExtra(TAG_POSITION, -1);

        /*length = intent.getLongExtra(TAG_LENGTH, -1);
        title = intent.getStringExtra(TAG_TITLE);
        progr = intent.getLongExtra(TAG_PROGRESS, -1);
        maxProgress = intent.getLongExtra(TAG_MAXPROGRESS, -1);*/

        job = gson.fromJson(intent.getStringExtra(TAG_JOB), Job.class);

        if (position == -1) return;
        serviceRunning = true;

        /*long length = Job.jobs.get(position).getLength();
        String title = Job.jobs.get(position).getTitle();*/

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent actionStopBroRec = new Intent(/*this, StopBroadcastReceiver.class*/);
        actionStopBroRec.setAction(TAG_ACTION);
        PendingIntent penActionStopBroRec = PendingIntent.getBroadcast(this, 0,
                actionStopBroRec, 0);
        Intent actionPauseBroRec = new Intent(/*this, PauseBroadcastReceiver.class*/);
        actionPauseBroRec.setAction(PAUSE_ACTION);
        actionPauseBroRec.putExtra(TAG_POSITION, position); //                               need to be fixed
        actionPauseBroRec.putExtra(TAG_JOB, gson.toJson(job));
        //actionPauseBroRec.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent penActionPauseBroRec = PendingIntent.getBroadcast(this, 0,
                actionPauseBroRec, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getNotification(/*position,*/ pendingIntent, penActionStopBroRec, penActionPauseBroRec);
        startForeground(1, notification);

//        for (long i = length; i >= 0 ; i-=1000) {
        while (job.getLength()/*length*/ >= 0) {
            Log.d(TAG, "onHandleIntent: isServiceRunning()" + isServiceRunning());

            actionPauseBroRec.putExtra(TAG_JOB, gson.toJson(job));
            penActionPauseBroRec = PendingIntent.getBroadcast(this, 0,
                    actionPauseBroRec, PendingIntent.FLAG_UPDATE_CURRENT);
            //Job.jobs.get(position).setLength(i); // (*)
            if (!serviceRunning) break; // moved this line of code from line before (*)
                                        // because progressBar progessing too fast
            job.setProgr(job.getProgr()+1000);//progr += 1000;//Job.jobs.get(position).setProgr(Job.jobs.get(position).getProgr()+1000);

            //notification = getNotification(position, pendingIntent, penActionStopBroRec, penActionPauseBroRec);
            notification = getNotification(pendingIntent, penActionStopBroRec, penActionPauseBroRec);
            mNotificationManager.notify(1, notification);
            SystemClock.sleep(1000);
            if (/*i*//*length*/job.getLength() == 0 && !/*title*/job.getTitle().contains("finished")) {
                job.setTitle(job.getTitle()+" | finished");//title += " | finished";//Job.jobs.get(position).setTitle(Job.jobs.get(position).getTitle()+" | finished");
                penActionPauseBroRec = null;
                serviceRunning = false;
                doVibrate();
                // without following method onDestoy() won't be called if Activity is destroyed
                stopSelf();

            }
            job.setLength(job.getLength()-1000); //length-= 1000;
        }
    }

    private Notification getNotification(/*int position,*/ PendingIntent pendingIntent,
                                         PendingIntent penActionStopBroRec, PendingIntent penActionPauseBroRec) {
        long length = job.getLength();//Job.jobs.get(position).getLength()*/;
        String title = job.getTitle();// Job.jobs.get(position).getTitle()*/;
        int hour = (int) ((length / 3600000) % 24);
        int min = (int) ((length / 60000) % 60);
        int sec = (int) (length / 1000) % 60;
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

                //.setProgress((int)Job.jobs.get(position).getMaxProgress(), (int)Job.jobs.get(position).getProgr(), false)
                .setProgress((int) job.getMaxProgress()/*maxProgress*/, (int)/*progr*/job.getProgr(), false)
                .setPriority(Notification.PRIORITY_LOW)

                .addAction(R.drawable.ic_stop_black_24dp, "Stop", penActionStopBroRec)
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", penActionPauseBroRec)

                .build();
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

        Log.d(TAG, "onDestroy: service");
    }
}
