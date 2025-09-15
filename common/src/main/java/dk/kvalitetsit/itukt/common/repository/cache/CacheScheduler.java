package dk.kvalitetsit.itukt.common.repository.cache;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;

public class CacheScheduler {

    private final Logger logger = LoggerFactory.getLogger(CacheScheduler.class);
    private final List<CacheLoader> loaders;
    private final TaskScheduler scheduler;

    public CacheScheduler(List<CacheLoader> loaders) {
        this.loaders = loaders;
        this.scheduler = getThreadPoolTaskScheduler(loaders);
    }

    private static ThreadPoolTaskScheduler getThreadPoolTaskScheduler(List<CacheLoader> loaders) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(loaders.size());
        taskScheduler.setThreadNamePrefix("cache-loader-");
        taskScheduler.initialize();
        return taskScheduler;
    }

    @PostConstruct
    public void init() {
        loaders.stream()
                .peek(this::load)
                .forEach(cache -> {
                    scheduler.schedule(() -> load(cache), new CronTrigger(cache.get_cron()));
                    logger.info("Scheduled loader {} with cron {} was registered", cache.getClass().getSimpleName(), cache.get_cron());
                });
    }

    private void load(CacheLoader cache) {
        try {
            cache.load();
            logger.info("Successfully loaded cache for {}", cache.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error while loading cache for {}", cache.getClass().getSimpleName(), e);
        }
    }
}





