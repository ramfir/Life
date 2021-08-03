package com.firda.secondlife;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class JobFragment extends Fragment implements View.OnClickListener {

    TextView mTextView;
    EditText title;
    EditText length;
    Button nextButton;

    public JobFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    private void initialize(View view) {
        mTextView = view.findViewById(R.id.textView);
        mTextView.setText("Enter the title and length of Job №" + String.valueOf(Job.jobs.size()+1));
        title = view.findViewById(R.id.titleEditText);
        length = view.findViewById(R.id.lengthEditText);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == nextButton.getId()) {
            String str = length.getText().toString();
            DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
            Date date;
            try {
                date = formatter.parse(str);
            } catch (ParseException e) {
                Toast.makeText(getActivity(),"Wrong time format", Toast.LENGTH_LONG).show();
                return;
            }
            Job.jobs.add(new Job(title.getText().toString(), date.getHours()*3600000+date.getMinutes()*60000+date.getSeconds()*1000));
            if (Job.jobs.size() == SetupActivity.jobsSize) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else {
                JobFragment jobFragment = new JobFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, jobFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }
    }
}
