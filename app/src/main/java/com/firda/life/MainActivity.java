package com.firda.life;

import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 21600000;
    private CountDownTimer mCountDownTimer;
    ArrayList<Integer> start_times;
    ArrayList<Button> buttons;
    ArrayList<TextView> up_counter;
    ArrayList<TextView> down_counter;
    private boolean mTimerRunning;
    public static String TAG1 = "MainActivity";
    public static String TAG2 = "AA";
    public static String TAG3 = "BB";
    public static String TAG4 = "CC";
    public static String TAG5 = "DD";
    public static String TAG6 = "EE";
    int mwhich = 2;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    public static int NOTIFICATION_ID = 1;
    public static long notifications = 1;
    String[] exercises;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG1, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG1, "onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG1, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG1, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG1, "onDestroy()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG1, "onCreate()");
        start_times = new ArrayList<Integer>();
        buttons = new ArrayList<>();
        up_counter = new ArrayList<>();
        down_counter = new ArrayList<>();
        if (savedInstanceState != null) {
            //Log.d(TAG1, "savedinstacestate!=null");
            start_times.add(savedInstanceState.getInt(TAG1));
            start_times.add(savedInstanceState.getInt(TAG2));
            start_times.add(savedInstanceState.getInt(TAG3));
            start_times.add(savedInstanceState.getInt(TAG4));
            mTimerRunning = savedInstanceState.getBoolean(TAG5);
            //Log.d(TAG1, String.valueOf(mTimerRunning));
            if (mTimerRunning) {
                mwhich = savedInstanceState.getInt(TAG6);
                startTimer(mwhich);
                //Log.d(TAG1, String.valueOf(mwhich));
            }

        } else {
           // Log.d(TAG1, "savedinstacestate==null");
            start_times.add(21600000);
            start_times.add(21600000);
            start_times.add(18000000);
            start_times.add(25200000);
        }
        for (int i = 0; i < 4; i++) {
            Log.d(TAG1, String.valueOf(start_times.get(i)));
        }
        buttons.add((Button) findViewById(R.id.button));
        buttons.add((Button) findViewById(R.id.button5));
        buttons.add((Button) findViewById(R.id.button6));
        buttons.add((Button) findViewById(R.id.button7));
        up_counter.add((TextView) findViewById(R.id.textView));
        up_counter.add((TextView) findViewById(R.id.textView2));
        up_counter.add((TextView) findViewById(R.id.textView3));
        up_counter.add((TextView) findViewById(R.id.textView4));
        down_counter.add((TextView) findViewById(R.id.textView5));
        down_counter.add((TextView) findViewById(R.id.textView6));
        down_counter.add((TextView) findViewById(R.id.textView7));
        down_counter.add((TextView) findViewById(R.id.textView8));
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < 4; j++)
                        buttons.get(j).setEnabled(true);
                    buttons.get(finalI).setEnabled(false);
                    mwhich = finalI;
                    startTimer(finalI);
                    /*if (mTimerRunning) {
                        pauseTimer();
                    } else {
                        startTimer(finalI);
                    }*/
                }
            });
            updateCountDownText(i);
        }
        exercises = new String[] {"Ад-дин", "Ихтисос", "Исы-Усы", "Хоб"};
        //startTimer();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(TAG1, start_times.get(0));
        savedInstanceState.putInt(TAG2, start_times.get(1));
        savedInstanceState.putInt(TAG3, start_times.get(2));
        savedInstanceState.putInt(TAG4, start_times.get(3));
        savedInstanceState.putBoolean(TAG5, mTimerRunning);
        savedInstanceState.putInt(TAG6, mwhich);
        //savedInstanceState.putInt(TAG, currentColor);
    }
    private void startTimer(final int which) {
        mwhich = which;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(start_times.get(which), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                start_times.set(which, (int) millisUntilFinished);
                notifications++;
                updateCountDownText(which);
            }

            @Override
            public void onFinish() {

                mwhich = (mwhich + 1) % 4;
                startTimer(mwhich);
            }
        }.start();

        //mTimerRunning = true;
    }
    /*private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }*/


    private void updateCountDownText(int which) {
        int seconds =  (start_times.get(which) / 1000) % 60 ;
        int minutes =  (start_times.get(which) / (1000*60)) % 60;
        int hours   =  (start_times.get(which) / (1000*60*60)) % 24;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        if (notifications % 60 == 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle(exercises[which] + ": Осталось")
                    .setContentText(timeLeftFormatted)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[] {0, 1000})
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID++, builder.build());

        }

        down_counter.get(which).setText(timeLeftFormatted);

        updateCountUpText(which);
    }
    private void updateCountUpText(int which) {
        int mill = 0;
        switch (which) {
            case (0):
                mill = 21600000 - start_times.get(which);
                break;
            case (1):
                mill = 21600000 - start_times.get(which);
                break;
            case (2):
                mill = 18000000 - start_times.get(which);
                break;
            case (3):
                mill = 25200000 - start_times.get(which);
                break;
        }
        int seconds =  (mill / 1000) % 60 ;
        int minutes =  (mill / (1000*60)) % 60;
        int hours   =  (mill / (1000*60*60)) % 24;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        up_counter.get(which).setText(timeLeftFormatted);
    }

}
