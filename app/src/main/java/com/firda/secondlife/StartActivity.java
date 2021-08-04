package com.firda.secondlife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class StartActivity extends AppCompatActivity {

    EditText jobsEdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Job.jobs.add(new Job("First job", 20000));
        Job.jobs.add(new Job("Second job", 5000));
        //if (Job.jobs.size() > 0) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        //}

        jobsEdText = findViewById(R.id.jobsEditText);

    }

    public void submit(View view) {
        try {
            Intent intent = new Intent(this, SetupActivity.class);
            intent.putExtra(SetupActivity.KEYJOBS, Integer.valueOf(jobsEdText.getText().toString()));
            startActivity(intent);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Wrong input format", Toast.LENGTH_LONG).show();
        }

    }
}
