package com.firda.secondlife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {
    private String title;
    private long length;
    private long progr;
    private long maxProgress; // this field exists to prevent progressBar's max value from changing

    public static List<Job> jobs = new ArrayList<>();

    Job(String title, long length) {
        this.title = title;
        this.length = length;
        this.maxProgress = length;
        this.progr = 0;
    }

    public String getTitle() {
        return title;
    }
    public long getLength() {
        return length;
    }
    public void setLength(long length) { this.length = length; }
    public void setTitle(String title) { this.title = title; }
    public void setProgr(long progr) { this.progr = progr; }
    public long getProgr() { return progr; }
    public long getMaxProgress() { return maxProgress; }
}
