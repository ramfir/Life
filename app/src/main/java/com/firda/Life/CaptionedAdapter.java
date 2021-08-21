package com.firda.Life;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class CaptionedAdapter extends
        RecyclerView.Adapter<CaptionedAdapter.ViewHolder> {

    private Listener listener;

    interface Listener {
        void onClick(int position);
    }

    private List<Job> jobs;
    int selected_position = RecyclerView.NO_POSITION;

    public CaptionedAdapter(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void setListener(Listener listener) {
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
            ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                                               .inflate(R.layout.card_captioned, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String title = jobs.get(position).getTitle();
        long progr = jobs.get(position).getProgr();
        long maxProgress = jobs.get(position).getMaxProgress();
        long length = jobs.get(position).getLength();
        final CardView cardView = holder.cardView;

        TextView titleTxtView = cardView.findViewById(R.id.titleTxtView);
        titleTxtView.setText(title);

        ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);
        progressBar.setMax((int) maxProgress);
        progressBar.setProgress(123); // workaround bug shorturl.at/lA038
        progressBar.setProgress((int) progr);

        TextView lengthTxtView = cardView.findViewById(R.id.lengthTxtView);
        NumberFormat f = new DecimalFormat("00");
        long hour = (length / 3600) % 24;
        long min = (length / 60) % 60;
        long sec = length % 60;
        lengthTxtView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));

        cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#FF008577") : Color.WHITE);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                    notifyItemChanged(selected_position);
                    selected_position = holder.getAdapterPosition();
                    notifyItemChanged(selected_position);
                }
            }
        });
    }
}
