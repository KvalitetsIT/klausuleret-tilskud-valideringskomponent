package dk.kvalitetsit.itukt.common.repository.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;

/**
 * The responsibility of this class is to schedule the load/reload of the caches that lies between the service and the repository layers
 */
public class CacheScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CacheScheduler.class);

    private static ThreadPoolTaskScheduler getThreadPoolTaskScheduler(List<CacheLoader> loaders) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(loaders.size());
        taskScheduler.setThreadNamePrefix("cache-loader-");
        taskScheduler.initialize();
        return taskScheduler;
    }

    public static void init(List<CacheLoader> loaders) {
        var scheduler = getThreadPoolTaskScheduler(loaders);

        loaders.stream()
                .peek(CacheScheduler::load)
                .forEach(cache -> {
                    scheduler.schedule(() -> load(cache), new CronTrigger(cache.getCron()));
                    logger.info("Scheduled loader {} with cron {} was registered", cache.getClass().getSimpleName(), cache.getCron());
                });
    }

    private static void load(CacheLoader cache) {
        try {
            cache.load();
            logger.info("Successfully loaded cache for {}", cache.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error while loading cache for {}", cache.getClass().getSimpleName(), e);
        }
    }
}





