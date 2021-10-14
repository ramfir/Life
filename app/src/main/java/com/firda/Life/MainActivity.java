package com.firda.Life;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.firda.Life.TimerService.PAUSE_ACTION;
import static com.firda.Life.TimerService.TAG_ACTION;
import static com.firda.Life.TimerService.TAG_JOB;
import static com.firda.Life.TimerService.TAG_POSITION;
import static com.firda.Life.PauseBroadcastReceiver.PLAY_ACTION;

public class MainActivity extends AppCompatActivity implements MyDialog.MyDialogListener {

    public static final String TAG = "MainActivity";
    final Handler handler = new Handler();
    Runnable runnable;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Gson gson = new Gson();
    List<Task> tasks = new ArrayList<>();
    int position = -1;
    String title;
    private CaptionedAdapter adapter;
    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0,
                                                                                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (position == viewHolder.getAdapterPosition())
                stopService();
            /*mEditor.remove(tasks.get(viewHolder.getAdapterPosition()).getTitle());
            mEditor.apply();*/
            tasks.remove(viewHolder.getAdapterPosition());
            position--;
            if (myService != null && myService.isServiceRunning()) {
                myService.setPosition(position);
            }
            adapter.setSelected_position(position);
            saveJobs();
            adapter.notifyDataSetChanged();
        }
    };
    private RecyclerView myRecyclerView;
    private TimerService myService;
    private ServiceConnection mServiceConnection;
    private boolean isBound;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        initJobs();
        Log.d(TAG, "onCreate: " + tasks);
        setupRecyclerView();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (myService != null && myService.isServiceRunning()) {
                    position = myService.getPosition();
                    tasks.set(position, myService.getTask());
                    //Log.d(TAG, "run: " + myService.getTask());
                    adapter.notifyDataSetChanged();
                    handler.postDelayed(this, 1000);
                } else {
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

    private void setupRecyclerView() {
        myRecyclerView = findViewById(R.id.my_recycler);
        adapter = new CaptionedAdapter(tasks);
        myRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(myRecyclerView);
        adapter.setListener(new CaptionedAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if (tasks.get(position).getDuration() == 0)
                    return;
                stopService();
                Intent serviceIntent = new Intent(getApplicationContext(), TimerService.class);
                serviceIntent.putExtra(TAG_POSITION, position);
                //serviceIntent.putExtra(TAG_JOB, gson.toJson(tasks.get(position)));
                serviceIntent.putExtra(TAG_JOB, gson.toJson(tasks));
                startService(serviceIntent);
                bindService();
            }
        });
    }

    private void initJobs() {
        String tasksInJson = mSharedPreferences.getString("tasks", "");
        if (!tasksInJson.equals("")) {
            Type tasksListType = new TypeToken<List<Task>>(){}.getType();
            tasks = gson.fromJson(tasksInJson, tasksListType);
        }
        /*Map<String, ?> keys = mSharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            tasks.add(gson.fromJson(entry.getValue().toString(), Task.class));
        }*/
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
            }
        };
        registerReceiver(pauseBroadcastReceiver, filterPause);

        IntentFilter filterPlay = new IntentFilter(PLAY_ACTION);
        BroadcastReceiver playBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bindService();
            }
        };
        registerReceiver(playBroadcastReceiver, filterPlay);
    }

    private void bindService() {
        if (mServiceConnection == null) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    TimerService.MyBinder myServiceBinder = (TimerService.MyBinder) service;
                    myService = myServiceBinder.getService();
                    adapter.setSelected_position(myService.getPosition());
                    handler.postDelayed(runnable, 500);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isBound = false;
                }
            };
        }
        Intent intent = new Intent(this, TimerService.class);
        if (bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE))
            isBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myService != null && myService.isServiceRunning())
            handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveJobs();
    }

    private void saveJobs() {
        String tasksInJson = gson.toJson(tasks);
        mEditor.putString("tasks", tasksInJson);
        mEditor.apply();
        /*for (int i = 0; i < tasks.size(); i++) {
            mEditor.putString(tasks.get(i).getTitle(), gson.toJson(tasks.get(i)));
        }
        mEditor.apply();*/
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, TimerService.class);
        stopService(serviceIntent);
        if (isBound) {
            isBound = false;
            unbindService(mServiceConnection);
        }
    }

    public void addDialog(View view) {
        MyDialog myDialog = new MyDialog();
        myDialog.show(getSupportFragmentManager(), "My Dialog");
    }

    @Override
    public void addJob(String title, String length) {
        DateFormat formatter = new SimpleDateFormat("hh:mm");
        Date date;
        try {
            date = formatter.parse(length);
        } catch (ParseException e) {
            Toast.makeText(this, "Wrong time format", Toast.LENGTH_LONG).show();
            return;
        }
        Task newTask = new Task(title,
                                date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds());
        /*String json = gson.toJson(newTask);

        mEditor.putString(title, json);
        mEditor.apply();*/

        tasks.add(newTask);
        saveJobs();
        adapter.notifyDataSetChanged();
    }
}
