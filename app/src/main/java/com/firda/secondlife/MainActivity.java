package com.firda.secondlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import static com.firda.secondlife.ExampleIntentService.TAG_ACTION;
import static com.firda.secondlife.ExampleIntentService.TAG_POSITION;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String TIMER_KEY = "TIMER_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView myRecyclerView = findViewById(R.id.my_recycler);
        final CaptionedAdapter adapter = new CaptionedAdapter(Job.jobs);
        myRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);

        adapter.setListener(new CaptionedAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if (Job.jobs.get(position).getLength() == 0) return;
                Intent serviceIntent = new Intent(getApplicationContext(), ExampleIntentService.class);
                serviceIntent.putExtra(TAG_POSITION, position);
                startService(serviceIntent);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TAG_ACTION);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long getMilliseconds = intent.getLongExtra(TIMER_KEY, 0);
                int position = intent.getIntExtra(TAG_POSITION, 0);
                Job.jobs.get(position).setLength(getMilliseconds);
                adapter.notifyDataSetChanged();

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void updateProgressBar() {

    }
    public void stopService(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        serviceIntent.putExtra("test", 97);
        stopService(serviceIntent);
    }
}
