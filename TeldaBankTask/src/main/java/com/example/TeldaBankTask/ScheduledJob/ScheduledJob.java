package com.example.TeldaBankTask.ScheduledJob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJob {
    private String jobId;
    private Runnable task;
    private long initialDelay;
    private long period;
    private TimeUnit timeUnit;
}
