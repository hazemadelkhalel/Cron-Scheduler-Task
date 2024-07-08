package com.example.TeldaBankTask.CronScheduler;

import com.example.TeldaBankTask.ScheduledJob.ScheduledJob;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class CronScheduler {
    private final ScheduledExecutorService executor;

    public CronScheduler() {
        this.executor = Executors.newScheduledThreadPool(4);
    }

    public void scheduleJob(ScheduledJob job) {
        ScheduledFuture<?> scheduledTask = executor.scheduleAtFixedRate(() -> {
            long startTime = System.currentTimeMillis();
            try {
                job.getTask().run();
            } finally {
                long executionTime = System.currentTimeMillis() - startTime;
                System.out.println("Job ID: " + job.getJobId() + " executed in " + executionTime + " ms");
            }
        }, job.getInitialDelay(), job.getPeriod(), job.getTimeUnit());
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
