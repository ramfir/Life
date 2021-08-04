package com.firda.secondlife;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;

public class CaptionedAdapter extends
        RecyclerView.Adapter<CaptionedAdapter.ViewHolder>{

    private Listener listener;
    interface Listener {
        void onClick(int position);
    }

    private List<Job> jobs;

    public CaptionedAdapter(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public CaptionedAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_captioned, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        CardView cardView = holder.cardView;
        TextView titleTxtView = cardView.findViewById(R.id.titleTxtView);
        titleTxtView.setText(jobs.get(position).getTitle());
        TextView lengthTxtView = cardView.findViewById(R.id.lengthTxtView);
        ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);
        progressBar.setProgress((int) jobs.get(position).progr);
        //int maxProgres = (int) jobs.get(position).getLength();
        progressBar.setMax((int) jobs.get(position).maxProgress);

        long milliSeconds = (long) (jobs.get(position).getLength());
        NumberFormat f = new DecimalFormat("00");
        long hour = (milliSeconds / 3600000) % 24;
        long min = (milliSeconds / 60000) % 60;
        long sec = (milliSeconds / 1000) % 60;
        lengthTxtView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }
}
