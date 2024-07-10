package com.example.TeldaBankTask;

import com.example.TeldaBankTask.model.Job;
import com.example.TeldaBankTask.service.ScratchScheduler;
import org.junit.jupiter.api.*;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScratchSchedulerTest {

    private ScratchScheduler scratchScheduler;

    @BeforeEach
    public void setup() {
        scratchScheduler = new ScratchScheduler();
    }

    @Test
    public void testJobSchedulingAndExecution() throws InterruptedException {
        Job job = mock(Job.class);
        when(job.getInitialDelay()).thenReturn(0L);
        when(job.getPeriod()).thenReturn(1000L); // 1 second period
        when(job.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);

        String jobId = "testJob";
        scratchScheduler.scheduleJob(jobId, job);

        // Allow time for the job to potentially run
        Thread.sleep(1500); // Wait for more than one period to ensure it runs at least once

        verify(job, atLeast(1)).execute();

        // Clean up
        scratchScheduler.stopJob(jobId);
    }

    @Test
    public void testJobCancellation() throws InterruptedException {
        Job job = mock(Job.class);
        when(job.getInitialDelay()).thenReturn(0L);
        when(job.getPeriod()).thenReturn(500L); // 0.5 seconds period
        when(job.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);

        String jobId = "cancelJob";
        scratchScheduler.scheduleJob(jobId, job);

        Thread.sleep(650); // Allow job to run at least once
        scratchScheduler.stopJob(jobId);

        // Wait a little after cancellation to check if job stops
        Thread.sleep(650);

        // The job should not run after being cancelled
        verify(job, atMost(2)).execute();
    }

    @Test
    public void testJobNotRunningBeforeInitialDelay() throws InterruptedException {
        Job job = mock(Job.class);
        when(job.getInitialDelay()).thenReturn(1000L); // 1 second delay
        when(job.getPeriod()).thenReturn(1000L);
        when(job.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);

        String jobId = "delayedJob";
        scratchScheduler.scheduleJob(jobId, job);

        // Check immediately; job should not have run yet
        verify(job, never()).execute();

        // Wait for longer than the initial delay and check again
        Thread.sleep(1200); // Wait beyond the initial delay
        verify(job, atLeastOnce()).execute();

        // Clean up
        scratchScheduler.stopJob(jobId);
    }

    @Test
    public void testMultipleJobsScheduling() throws InterruptedException {
        Job job1 = mock(Job.class);
        Job job2 = mock(Job.class);
        when(job1.getInitialDelay()).thenReturn(0L);
        when(job1.getPeriod()).thenReturn(1000L); // Run every 1 second
        when(job1.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);
        when(job2.getInitialDelay()).thenReturn(0L);
        when(job2.getPeriod()).thenReturn(1500L); // Run every 1.5 seconds
        when(job2.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);

        scratchScheduler.scheduleJob("job1", job1);
        scratchScheduler.scheduleJob("job2", job2);

        // Allow time for both jobs to potentially run
        Thread.sleep(3000); // 3 seconds should allow multiple runs

        verify(job1, atLeast(2)).execute();
        verify(job2, atLeast(1)).execute();

        // Clean up
        scratchScheduler.stopJob("job1");
        scratchScheduler.stopJob("job2");
    }

    @Test
    public void testExceptionHandlingInJob() throws InterruptedException {
        Job job = mock(Job.class);
        doThrow(new RuntimeException("Forced exception")).when(job).execute();
        when(job.getInitialDelay()).thenReturn(0L);
        when(job.getPeriod()).thenReturn(1000L);
        when(job.getTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);

        String jobId = "exceptionJob";
        scratchScheduler.scheduleJob(jobId, job);

        Thread.sleep(1500); // Wait a bit to allow the job to execute and throw

        // Job's execute method should still be called despite exceptions
        verify(job, atLeast(1)).execute();

        // Clean up
        scratchScheduler.stopJob(jobId);
    }

    @AfterAll
    public void tearDown() {
        // Ensure all threads are stopped after tests
        scratchScheduler.getJobThreads().keySet().forEach(scratchScheduler::stopJob);
    }
}
