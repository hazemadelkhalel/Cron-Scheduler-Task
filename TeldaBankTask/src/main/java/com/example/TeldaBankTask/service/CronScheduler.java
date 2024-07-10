package com.example.TeldaBankTask.service;

import com.example.TeldaBankTask.model.ScheduledJob;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Getter
public class CronScheduler {
    private final ScheduledExecutorService executor;

    public CronScheduler() {
        this.executor = Executors.newScheduledThreadPool(4);
    }

    public CronScheduler(ScheduledExecutorService executorService) {
        this.executor = executorService;
    }

    public ScheduledFuture<?> scheduleJob(ScheduledJob job) {
        if (job == null || job.getTask() == null) {
            throw new NullPointerException("Job or task cannot be null.");
        }
        ScheduledFuture<?> scheduledTask = executor.scheduleAtFixedRate(() -> {
            long startTime = System.currentTimeMillis();
            try {
                job.getTask().run();
            } finally {
                long executionTime = System.currentTimeMillis() - startTime;
                System.out.println("Job ID: " + job.getJobId() + " executed in " + executionTime + " ms");
            }
        }, job.getInitialDelay(), job.getPeriod(), job.getTimeUnit());
        return scheduledTask;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown(); // Trigger the orderly shutdown
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force shutdown if tasks did not terminate
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
