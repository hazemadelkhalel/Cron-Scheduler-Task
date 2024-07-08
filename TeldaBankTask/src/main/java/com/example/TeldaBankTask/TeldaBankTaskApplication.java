package com.example.TeldaBankTask;

import com.example.TeldaBankTask.CronScheduler.CronScheduler;
import com.example.TeldaBankTask.ScheduledJob.ScheduledJob;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class TeldaBankTaskApplication {
	private AtomicInteger x = new AtomicInteger(0);

	public static void main(String[] args) {
		SpringApplication.run(TeldaBankTaskApplication.class, args);
	}
	@Bean
	public CommandLineRunner schedulingRunner(CronScheduler scheduler) {
		return args -> {
			Runnable task1 = () -> System.out.println("Scheduled task running at " + new java.util.Date());
			Runnable task2 = () -> System.out.println("Scheduled task running at " + new java.util.Date());
			Runnable task3 = () -> System.out.println("Scheduled task running at " + new java.util.Date());
            Runnable task4 = incrementValueX();
            Runnable task5 = printValueX();


			ScheduledJob job1 = new ScheduledJob("printTime1", task1, 0, 5, TimeUnit.SECONDS);
			ScheduledJob job2 = new ScheduledJob("printTime2", task2, 5,  10, TimeUnit.SECONDS);
            ScheduledJob job3 = new ScheduledJob("printTime3", task3, 10, 15, TimeUnit.SECONDS);
            ScheduledJob job4 = new ScheduledJob("incrementX", task4, 2, 5, TimeUnit.SECONDS);
            ScheduledJob job5 = new ScheduledJob("printX", task5, 2, 6, TimeUnit.SECONDS);

            scheduler.scheduleJob(job1);
			scheduler.scheduleJob(job2);
			scheduler.scheduleJob(job3);
			scheduler.scheduleJob(job4);
			scheduler.scheduleJob(job5);
		};
	}

    public Runnable incrementValueX() {
        return () -> x.incrementAndGet();
    }

    public Runnable printValueX() {
        return () -> System.out.println("The Value of X is " + x.get());
    }
}
