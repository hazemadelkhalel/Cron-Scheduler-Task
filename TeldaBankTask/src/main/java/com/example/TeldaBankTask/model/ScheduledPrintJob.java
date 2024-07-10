package com.example.TeldaBankTask.model;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ScheduledPrintJob implements Job {
    private final String message;
    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;

    public ScheduledPrintJob(String message, long initialDelay, long period, TimeUnit timeUnit) {
        this.message = message;
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    @Override
    public void execute() {
        System.out.println(message + " at " + new java.util.Date());
    }

    @Override
    public long getInitialDelay() {
        return initialDelay;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
