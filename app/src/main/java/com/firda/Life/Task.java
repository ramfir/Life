package com.firda.Life;

public class Task {
    private String title;
    private long duration; // in seconds
    private long progr;
    private long maxProgress; // this field exists to prevent progressBar's max value from changing

    Task(String title, long duration) {
        this.title = title;
        this.duration = duration;
        this.maxProgress = duration;
        this.progr = 0;
    }

    public String getTitle() {
        return title;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) { this.duration = duration; }
    public void setTitle(String title) { this.title = title; }
    public void setProgr(long progr) { this.progr = progr; }
    public long getProgr() { return progr; }
    public long getMaxProgress() { return maxProgress; }

    @Override
    public String toString() {
        return title + " " + String.valueOf(duration);
    }
}
