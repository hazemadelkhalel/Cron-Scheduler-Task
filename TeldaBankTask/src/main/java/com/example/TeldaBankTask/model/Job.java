package com.example.TeldaBankTask.model;

import java.util.concurrent.TimeUnit;

public interface Job {
    void execute();

    long getInitialDelay();

    long getPeriod();

    TimeUnit getTimeUnit();
}
