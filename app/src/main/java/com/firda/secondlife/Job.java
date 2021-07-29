package com.firda.secondlife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {
    String title;
    double length;

    public static List<Job> jobs = new ArrayList<>() /* = Arrays.asList(
            new Job("Git", 1),
            new Job("Project", 2),
            new Job("SQL", 1))*/;

    Job(String title, double length) {
        this.title = title;
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public double getLength() {
        return length;
    }
}
