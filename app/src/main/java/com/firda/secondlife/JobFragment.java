package com.firda.secondlife;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class JobFragment extends Fragment implements View.OnClickListener {

    TextView mTextView;
    EditText title;
    EditText length;
    List<Job> jobs;
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
        jobs = Job.jobs;
        mTextView = view.findViewById(R.id.textView);
        mTextView.setText("Enter the title and length of Job №" + String.valueOf(jobs.size()+1));
        title = view.findViewById(R.id.titleEditText);
        length = view.findViewById(R.id.lengthEditText);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == nextButton.getId()) {
            jobs.add(new Job(title.getText().toString(), Double.valueOf(length.getText().toString())));
            if (jobs.size() == SetupActivity.jobsSize) {
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
