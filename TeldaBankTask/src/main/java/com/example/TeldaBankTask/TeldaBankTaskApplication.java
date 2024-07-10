package com.example.TeldaBankTask;

import com.example.TeldaBankTask.model.ScheduledJob;
import com.example.TeldaBankTask.model.ScheduledPrintJob;
import com.example.TeldaBankTask.service.CronScheduler;
import com.example.TeldaBankTask.service.ScratchScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class TeldaBankTaskApplication {

	public static void main(String[] args) {
		ScratchScheduler scheduler = new ScratchScheduler();

		ScheduledPrintJob job1 = new ScheduledPrintJob("Job 1 runs every 5 seconds", 0, 2, TimeUnit.SECONDS);
		ScheduledPrintJob job2 = new ScheduledPrintJob("Job 2 runs every 10 seconds", 2, 4, TimeUnit.SECONDS);
		ScheduledPrintJob job3 = new ScheduledPrintJob("Job 3 runs every 15 seconds", 4, 6, TimeUnit.SECONDS);

		scheduler.scheduleJob("job1", job1);
		scheduler.scheduleJob("job2", job2);
		scheduler.scheduleJob("job3", job3);

	}

}
