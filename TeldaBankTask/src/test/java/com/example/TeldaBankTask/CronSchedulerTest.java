package com.example.TeldaBankTask;

import com.example.TeldaBankTask.service.CronScheduler;
import com.example.TeldaBankTask.model.ScheduledJob;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CronSchedulerTest {
    @MockBean
    private ScheduledExecutorService executorServiceMock;

    private CronScheduler cronScheduler;

    @BeforeEach
    public void setup() {
        cronScheduler = new CronScheduler(executorServiceMock);
        // Mock the executor service to return a mock ScheduledFuture when scheduleAtFixedRate is called
        ScheduledFuture mockFuture = mock(ScheduledFuture.class);
        when(mockFuture.cancel(false)).thenReturn(true);
        when(executorServiceMock.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(mockFuture);
    }

    @Test
    public void testJobSchedulingWithZeroDelayAndPeriod() {
        Runnable task = mock(Runnable.class);
        ScheduledJob job = new ScheduledJob("zeroDelayJob", task, 0, 0, TimeUnit.SECONDS);

        cronScheduler.scheduleJob(job);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor()).scheduleAtFixedRate(runnableCaptor.capture(), eq(0L), eq(0L), eq(TimeUnit.SECONDS));

        runnableCaptor.getValue().run();
        verify(task).run();
    }

    @Test
    public void testSchedulingNullTask() {
        ScheduledJob job = new ScheduledJob("nullTaskJob", null, 1, 1, TimeUnit.SECONDS);

        assertThrows(NullPointerException.class, () -> {
            cronScheduler.scheduleJob(job);
        });
    }

    @Test
    public void testJobExecutionFrequency() throws InterruptedException {
        Runnable task = mock(Runnable.class);
        ScheduledJob job = new ScheduledJob("frequentJob", task, 0, 100, TimeUnit.MILLISECONDS);

        ScheduledFuture future = mock(ScheduledFuture.class);
        when(executorServiceMock.scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(100L), eq(TimeUnit.MILLISECONDS)))
                .thenReturn(future);

        cronScheduler.scheduleJob(job);

        // Use ArgumentCaptor to capture the runnable that is actually scheduled
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceMock).scheduleAtFixedRate(runnableCaptor.capture(), eq(0L), eq(100L), eq(TimeUnit.MILLISECONDS));

        // Manually run the captured runnable the desired number of times
        Runnable capturedRunnable = runnableCaptor.getValue();
        for (int i = 0; i < 5; i++) {
            capturedRunnable.run();
        }

        // Verify that the task has been run at least 4 times but no more than 6 times
        verify(task, atLeast(4)).run();
        verify(task, atMost(6)).run();
    }



    @Test
    public void testJobCancellation() {
        Runnable task = mock(Runnable.class);
        ScheduledJob job = new ScheduledJob("cancelJob", task, 0, 1, TimeUnit.SECONDS);

        // Create the mock ScheduledFuture with correct type information
        ScheduledFuture future = mock(ScheduledFuture.class);
        when(future.cancel(false)).thenReturn(true); // Ensure cancellation returns true
        when(future.isCancelled()).thenReturn(false).thenReturn(true); // Change state after cancellation

        // Stub the scheduleAtFixedRate to return the prepared mock future
        when(executorServiceMock.scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(TimeUnit.SECONDS)))
                .thenReturn(future);

        // Schedule the job
        ScheduledFuture<?> scheduledFuture = cronScheduler.scheduleJob(job);
        Assertions.assertFalse(scheduledFuture.isCancelled()); // Check it is not cancelled initially

        // Cancel the job
        boolean cancelled = scheduledFuture.cancel(false);
        Assertions.assertTrue(cancelled); // Ensure it reports it was cancelled
        Assertions.assertTrue(scheduledFuture.isCancelled()); // Ensure future reflects cancellation
    }




    @Test
    public void testHighLoadScheduling() {
        int jobCount = 1000;
        Runnable task = () -> {
        };
        for (int i = 0; i < jobCount; i++) {
            ScheduledJob job = new ScheduledJob("job" + i, task, 0, 1, TimeUnit.SECONDS);
            cronScheduler.scheduleJob(job);
        }

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor(), times(jobCount)).scheduleAtFixedRate(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testJobScheduling() {
        Runnable task1 = mock(Runnable.class);
        Runnable task2 = mock(Runnable.class);
        Runnable task3 = mock(Runnable.class);
        Runnable task4 = mock(Runnable.class);
        Runnable task5 = mock(Runnable.class);
        ScheduledJob job1 = new ScheduledJob("job1", task1, 0, 1, TimeUnit.SECONDS);
        ScheduledJob job2 = new ScheduledJob("job2", task2, 1, 2, TimeUnit.SECONDS);
        ScheduledJob job3 = new ScheduledJob("job3", task3, 2, 3, TimeUnit.SECONDS);
        ScheduledJob job4 = new ScheduledJob("job4", task4, 3, 4, TimeUnit.SECONDS);
        ScheduledJob job5 = new ScheduledJob("job5", task5, 4, 5, TimeUnit.SECONDS);

        cronScheduler.scheduleJob(job1);
        cronScheduler.scheduleJob(job2);
        cronScheduler.scheduleJob(job3);
        cronScheduler.scheduleJob(job4);
        cronScheduler.scheduleJob(job5);

        // Verify scheduleAtFixedRate was called with the correct parameters
        verify(cronScheduler.getExecutor()).scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(TimeUnit.SECONDS));

        // Invoke the task to simulate execution and capture the Runnable that was passed to scheduleAtFixedRate
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor(), times(5)).scheduleAtFixedRate(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));

        for (Runnable capturedRunnable : runnableCaptor.getAllValues()) {
            capturedRunnable.run();
        }

        // Verify that the task's run method was actually called
        verify(task1).run();
        verify(task2).run();
        verify(task3).run();
        verify(task4).run();
        verify(task5).run();
    }

    @Test
    public void testJobSchedulingWithExceptions() {
        Runnable task = mock(Runnable.class);
        doThrow(new RuntimeException("Test Exception")).when(task).run();
        ScheduledJob job = new ScheduledJob("exceptionJob", task, 0, 1, TimeUnit.SECONDS);

        cronScheduler.scheduleJob(job);

        // Capture and run the scheduled task
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor()).scheduleAtFixedRate(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));
        Runnable capturedRunnable = runnableCaptor.getValue();

        try {
            capturedRunnable.run();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Test Exception"));
        }

        // Ensure exception is handled and doesn't disrupt other tasks
        verify(task).run();
    }

    @Test
    public void testJobOverlapHandling() throws InterruptedException {
        Runnable task1 = mock(Runnable.class);
        Runnable task2 = mock(Runnable.class);
        ScheduledJob job1 = new ScheduledJob("overlapJob1", task1, 0, 1, TimeUnit.SECONDS);
        ScheduledJob job2 = new ScheduledJob("overlapJob2", task2, 0, 1, TimeUnit.SECONDS);

        cronScheduler.scheduleJob(job1);
        cronScheduler.scheduleJob(job2);

        // Allow time for the jobs to run
        Thread.sleep(1500);

        // Capture and run the scheduled tasks
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor(), times(2)).scheduleAtFixedRate(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));

        for (Runnable capturedRunnable : runnableCaptor.getAllValues()) {
            capturedRunnable.run();
        }

        verify(task1, atLeast(1)).run();
        verify(task2, atLeast(1)).run();
    }

    @Test
    public void testJobRescheduling() throws InterruptedException {
        Runnable task = mock(Runnable.class);
        ScheduledJob job = new ScheduledJob("rescheduleJob", task, 0, 1, TimeUnit.SECONDS);

        cronScheduler.scheduleJob(job);

        // Allow time for the job to run initially
        Thread.sleep(500);
        job.setInitialDelay(2);
        job.setPeriod(2);
        cronScheduler.scheduleJob(job);

        // Allow time for the job to run again with new schedule
        Thread.sleep(3000);

        // Capture and run the scheduled task
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(cronScheduler.getExecutor(), atLeast(2)).scheduleAtFixedRate(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));

        for (Runnable capturedRunnable : runnableCaptor.getAllValues()) {
            capturedRunnable.run();
        }

        verify(task, atLeast(2)).run();
    }

    @Test
    public void testShutdownHandling() throws InterruptedException {
        // Call shutdown on the cronScheduler
        cronScheduler.shutdown();

        // Verify that shutdown was called on executorServiceMock
        verify(executorServiceMock).shutdown();
        verify(executorServiceMock).awaitTermination(anyLong(), any(TimeUnit.class));
        // Optionally, verify shutdownNow if expected in your scenario
        verify(executorServiceMock).shutdownNow();
    }


    @AfterAll
    public void tearDown() {
        cronScheduler.shutdown();
    }
}
