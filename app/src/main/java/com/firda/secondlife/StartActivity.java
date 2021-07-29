package com.firda.secondlife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class StartActivity extends AppCompatActivity {

    EditText jobsEdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        jobsEdText = findViewById(R.id.jobsEditText);
    }

    public void submit(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putExtra(SetupActivity.KEYJOBS, Integer.valueOf(jobsEdText.getText().toString()));

        startActivity(intent);
    }
}
