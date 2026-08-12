package dk.kvalitetsit.itukt.common.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;

/**
 * The responsibility of this class is to schedule execution of the {@link ScheduledJob}'s
 */
public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private static ThreadPoolTaskScheduler getThreadPoolTaskScheduler(List<ScheduledJob> jobs) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(jobs.size());
        taskScheduler.setThreadNamePrefix("scheduled-job-");
        taskScheduler.initialize();
        return taskScheduler;
    }

    public static void init(List<ScheduledJob> jobs) {
        var scheduler = getThreadPoolTaskScheduler(jobs);

        jobs.stream()
                .peek(JobScheduler::run)
                .forEach(job -> {
                    scheduler.schedule(() -> run(job), new CronTrigger(job.getCron()));
                    logger.info("Scheduled job {} with cron {} was registered", job.getClass().getSimpleName(), job.getCron());
                });
    }

    private static void run(ScheduledJob job) {
        try {
            job.run();
            logger.info("Successfully executed job for {}", job.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error while executing job for {}", job.getClass().getSimpleName(), e);
        }
    }
}





