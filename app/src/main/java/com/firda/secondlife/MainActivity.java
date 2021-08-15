package com.firda.secondlife;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.firda.secondlife.ExampleIntentService.PAUSE_ACTION;
import static com.firda.secondlife.ExampleIntentService.TAG_ACTION;
import static com.firda.secondlife.ExampleIntentService.TAG_JOB;
import static com.firda.secondlife.ExampleIntentService.TAG_LENGTH;
import static com.firda.secondlife.ExampleIntentService.TAG_MAXPROGRESS;
import static com.firda.secondlife.ExampleIntentService.TAG_POSITION;
import static com.firda.secondlife.ExampleIntentService.TAG_PROGRESS;
import static com.firda.secondlife.ExampleIntentService.TAG_TITLE;
import static com.firda.secondlife.PauseBroadcastReceiver.PLAY_ACTION;

public class MainActivity extends AppCompatActivity implements MyDialog.MyDialogListener {

    public static final String TAG = "MainActivity";
    public static final String TIMER_KEY = "TIMER_KEY";

    CaptionedAdapter adapter;
    RecyclerView myRecyclerView;

    private ExampleIntentService myService;
    private ServiceConnection mServiceConnection;
    private boolean isBound;

    final Handler handler = new Handler();
    Runnable runnable;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Gson gson = new Gson();
    List<Job> jobs = new ArrayList<>();

    int position;
    long length;
    long progr;
    String title;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        initJobs();
        /*Job.jobs.add(new Job("First job", 10000));
        Job.jobs.add(new Job("Second job", 15000));
        Job.jobs.add(new Job("Second job", 55000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));
        Job.jobs.add(new Job("Second job", 5000));*/

        myRecyclerView = findViewById(R.id.my_recycler);
        adapter = new CaptionedAdapter(jobs);
        myRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(myRecyclerView);
        adapter.setListener(new CaptionedAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if (jobs.get(position).getLength() == 0) return;
                stopService(myRecyclerView);
                Intent serviceIntent = new Intent(getApplicationContext(), ExampleIntentService.class);
                serviceIntent.putExtra(TAG_POSITION, position);
                /*serviceIntent.putExtra(TAG_LENGTH, jobs.get(position).getLength());
                serviceIntent.putExtra(TAG_TITLE, jobs.get(position).getTitle());
                serviceIntent.putExtra(TAG_PROGRESS, jobs.get(position).getProgr());
                serviceIntent.putExtra(TAG_MAXPROGRESS, jobs.get(position).getMaxProgress());*/
                serviceIntent.putExtra(TAG_JOB, gson.toJson(jobs.get(position)));
                startService(serviceIntent);
                bindService();
                handler.postDelayed(runnable, 1000);
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: myService==null " + (myService == null));
                Log.d(TAG, "run: myService.isServiceRunning() " + (myService.isServiceRunning()));
                if (myService != null && myService.isServiceRunning()) {
                    Log.d(TAG, "run: in if");
                    position = myService.getPosition();
                    /*length = myService.getLength();
                    progr = myService.getProgr();
                    title = myService.getTitle();
                    jobs.get(position).setLength(length);
                    jobs.get(position).setProgr(progr);
                    jobs.get(position).setTitle(title);*/
                    jobs.set(position, myService.getJob());
                    adapter.notifyDataSetChanged();
                    handler.postDelayed(this, 1000);
                } else {
                    //handler.removeCallbacks(this);
                    if (isBound) {
                        isBound = false;
                        unbindService(mServiceConnection);
                    }

                }
            }
        };

        bindService();
        adapter.notifyDataSetChanged();

        registerReceivers();
    }

    private void initJobs() {
        Map<String, ?> keys = mSharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            jobs.add(gson.fromJson(entry.getValue().toString(), Job.class));
        }
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter(TAG_ACTION);
        BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBound) {
                    isBound = false;
                    unbindService(mServiceConnection);
                }

                Log.d(TAG, "onReceive: stop");
            }
        };
        registerReceiver(stopBroadcastReceiver, filter);

        IntentFilter filterPause = new IntentFilter(PAUSE_ACTION);
        BroadcastReceiver pauseBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBound) {
                    isBound = false;
                    unbindService(mServiceConnection);
                }

                Log.d(TAG, "onReceive: pause");
            }
        };
        registerReceiver(pauseBroadcastReceiver, filterPause);

        IntentFilter filterPlay = new IntentFilter(PLAY_ACTION);
        BroadcastReceiver playBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bindService();

                Log.d(TAG, "onReceive: play");
            }
        };
        registerReceiver(playBroadcastReceiver, filterPlay);
    }

    private void bindService() {
        if (mServiceConnection == null) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ExampleIntentService.MyBinder myServiceBinder = (ExampleIntentService.MyBinder) service;
                    myService = myServiceBinder.getService();
                    handler.postDelayed(runnable, 500);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isBound = false;
                }
            };
        }

        if (bindService(new Intent(this, ExampleIntentService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE)) isBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveJobs();

    }

    private void saveJobs() {
        for (int i = 0; i < jobs.size(); i++) {
            mEditor.putString(jobs.get(i).getTitle(), gson.toJson(jobs.get(i)));
        }
        mEditor.apply();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (myService != null && myService.isServiceRunning())
            handler.post(runnable);
    }

    public void stopService(View view) {
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        stopService(serviceIntent);
        if (isBound) {
            isBound = false;
            unbindService(mServiceConnection);
        }

    }

    public void addJob(View view) {
        MyDialog myDialog = new MyDialog();
        myDialog.show(getSupportFragmentManager(), "My Dialog");
    }

    @Override
    public void add(String title, String length) {
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Date date;
        try {
            date = formatter.parse(length);
        } catch (ParseException e) {
            Toast.makeText(this,"Wrong time format", Toast.LENGTH_LONG).show();
            return;
        }

        Job newJob = new Job(title, date.getHours()*3600000+date.getMinutes()*60000+date.getSeconds()*1000);
        String json = gson.toJson(newJob);

        mEditor.putString(title, json);
        mEditor.apply();

        jobs.add(newJob);
        adapter.notifyDataSetChanged();
    }

    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            mEditor.remove(jobs.get(viewHolder.getAdapterPosition()).getTitle());
            mEditor.apply();
            jobs.remove(viewHolder.getAdapterPosition());

            adapter.notifyDataSetChanged();
        }
    };
}
