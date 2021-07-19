package com.firda.secondlife;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    TextView gitTime, projectTime;
    Button gitButton, projectButton;
    long workTime = 10800000;
    long projectworkTime = 10800000;
    CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gitTime = findViewById(R.id.gitTextView);
        projectTime = findViewById(R.id.projectTextView);
        gitButton = findViewById(R.id.gitButton);
        projectButton = findViewById(R.id.projectButton);

        updateTextView(gitTime, workTime);
        updateTextView(projectTime, projectworkTime);

        /*new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateTextView(gitTime, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gitTime.setText("00:00:00");
            }
        }.start();*/
    }

    void updateTextView(TextView view, long milliSeconds) {
        NumberFormat f = new DecimalFormat("00");
        long hour = (milliSeconds / 3600000) % 24;
        long min = (milliSeconds / 60000) % 60;
        long sec = (milliSeconds / 1000) % 60;
        view.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
    }

    public void git(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        serviceIntent.putExtra("time", workTime);
        serviceIntent.putExtra("work", "Git");

        startService(serviceIntent);
       /* if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            projectButton.setEnabled(true);
        }
        mCountDownTimer = new CountDownTimer(workTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                workTime = millisUntilFinished;
                updateTextView(gitTime, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gitTime.setText("00:00:00");
            }
        };
        mCountDownTimer.start();
        gitButton.setEnabled(false);*/
    }

    public void project(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        serviceIntent.putExtra("time", projectworkTime);
        serviceIntent.putExtra("work", "Project");

        startService(serviceIntent);
        /*if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            gitButton.setEnabled(true);
        }
        mCountDownTimer = new CountDownTimer(projectworkTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                projectworkTime = millisUntilFinished;
                updateTextView(projectTime, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gitTime.setText("00:00:00");
            }
        };
        mCountDownTimer.start();
        projectButton.setEnabled(false);*/
    }

    public void stopService(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        stopService(serviceIntent);
    }
}
