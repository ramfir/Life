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

    private List<Task> tasks;
    private int selected_position = RecyclerView.NO_POSITION;

    public CaptionedAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public void setSelected_position(int selected_position) { this.selected_position = selected_position;}

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
        String title = tasks.get(position).getTitle();
        long progr = tasks.get(position).getProgr();
        long maxProgress = tasks.get(position).getMaxProgress();
        long length = tasks.get(position).getDuration();
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

        cardView.setOnClickListener(new View.OnClickListener() { // bad decision to create anonymous class every time onBindViewHolder is called because it will be called many times. Move to ViewHolder class setting onClick listener to cardview
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
