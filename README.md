# In-Process Cron Scheduler

## Description

This project implements an in-process cron scheduler in Java using Spring Boot, designed to schedule and execute jobs periodically at specified intervals. The scheduler supports multiple concurrent job executions with the ability to select single-run expected intervals and scheduling frequencies. Each job is uniquely identified and executed according to the scheduled plan, with detailed logging of execution metrics such as execution time.

## Technical Decisions
- **Spring Boot Framework**: Leveraged for dependency management and simplifying application setup and configuration. Spring Boot allows for easy scaling of the project and seamless integration of additional components.

- **ScheduledExecutorService**: Used to manage thread scheduling and execution. This service helps efficiently manage concurrent tasks and supports periodic execution with fixed-rate and fixed-delay scheduling.

- **Lombok Library**: Employed to reduce boilerplate code in Java classes, such as getters, setters, and constructors, thereby making the code cleaner and more readable.

- **JUnit and Mockito for Testing**: Ensures the reliability of the scheduler through comprehensive unit tests that mock the underlying scheduling framework to test various scenarios like job execution, cancellation, and error handling.

## Trade-offs
- **Concurrency vs. Resource Utilization**: The scheduler uses a fixed thread pool size, which simplifies concurrency but limits the number of concurrent tasks that can run, potentially underutilizing system resources under low load and overloading under high load.

- **Error Handling**: Currently, the scheduler logs errors but does not retry failed executions, which might be necessary for critical tasks.

- **Scalability**: While suitable for applications with moderate load, the in-process nature of the scheduler may not handle extremely high loads as efficiently as distributed scheduling systems.

## Example Usage

```java
@SpringBootApplication
public class SchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    public CommandLineRunner schedulingRunner(CronScheduler scheduler) {
        return args -> {
            Runnable task = () -> System.out.println("Task executed at: " + new java.util.Date());
            ScheduledJob job = new ScheduledJob("task1", task, 0, 1, TimeUnit.MINUTES);
            scheduler.scheduleJob(job);
        };
    }
}
```

## Future Improvements
- **Dynamic Thread Pool**: Implementing a dynamic thread pool that adjusts based on the workload could optimize resource utilization and handle higher loads more effectively.

- **Fault Tolerance**: Introducing retry mechanisms and better error handling to improve the scheduler's robustness.

- **Distributed Scheduling**: Expanding the scheduler to support distributed systems could help in handling larger-scale applications, and spreading jobs across multiple nodes.

- **API for Job Management**: Developing a REST API to manage jobs could enable dynamic job scheduling and cancellation, improving the usability and flexibility of the scheduler.





