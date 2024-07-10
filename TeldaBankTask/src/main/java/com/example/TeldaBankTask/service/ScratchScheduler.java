package com.example.TeldaBankTask.service;

import com.example.TeldaBankTask.model.Job;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Getter
@Setter
public class ScratchScheduler {
    private final Map<String, Thread> jobThreads = new ConcurrentHashMap<>();

    public void scheduleJob(String jobId, Job job) {
        Thread jobThread = new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.convert(job.getInitialDelay(), job.getTimeUnit()));
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        long startTime = System.currentTimeMillis();
                        job.execute();
                        long sleepTime = TimeUnit.MILLISECONDS.convert(job.getPeriod(), job.getTimeUnit()) - (System.currentTimeMillis() - startTime);
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + jobId + " was interrupted during sleep");
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Exception in job execution for " + jobId + ": " + e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Initial sleep interrupted for job " + jobId);
            }
        });

        jobThread.start();
        jobThreads.put(jobId, jobThread);
    }


    public void stopJob(String jobId) {
        Thread jobThread = jobThreads.get(jobId);
        if (jobThread != null) {
            jobThread.interrupt();
            jobThreads.remove(jobId);
        }
    }
}
