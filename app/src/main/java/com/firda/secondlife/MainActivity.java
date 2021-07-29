package com.firda.secondlife;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    /*public static final String testString = "Project";
    public static final long testLong = 5000;
    public static int works = 2;
    TextView gitTime, projectTime;
    Button gitButton, projectButton;
    *//*public static *//*long workTime = 5000;//10800000;
    *//*public static *//*long projectworkTime = 5000;//10800000;
    CountDownTimer mCountDownTimer;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView myRecyclerView = findViewById(R.id.my_recycler);
        CaptionedAdapter adapter = new CaptionedAdapter(Job.jobs);
        myRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);

        adapter.setListener(new CaptionedAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent serviceIntent = new Intent(getApplicationContext(), ExampleIntentService.class);
                serviceIntent.putExtra("time", (long)(Job.jobs.get(position).getLength()*3600000));
                serviceIntent.putExtra("work", Job.jobs.get(position).getTitle());

                startService(serviceIntent);
            }
        });
        /*gitTime = findViewById(R.id.gitTextView);
        projectTime = findViewById(R.id.projectTextView);
        gitButton = findViewById(R.id.gitButton);
        projectButton = findViewById(R.id.projectButton);

        updateTextView(gitTime, workTime);
        updateTextView(projectTime, projectworkTime);*/

    }

   /* void updateTextView(TextView view, long milliSeconds) {
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
    }

    public void project(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        serviceIntent.putExtra("time", projectworkTime);
        serviceIntent.putExtra("work", "Project");

        startService(serviceIntent);
    }*/

    public void stopService(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        stopService(serviceIntent);

        //works = 2;
    }
}
