package dk.kvalitetsit.itukt.common.repository.cache;

/**
 * A simple facade which is exposed towards the {@link CacheScheduler}.
 * The purpose of the interface is to abstract away additional concerns which is not supposed to be accessible by the {@link CacheScheduler}
 */
public interface CacheLoader {
    /**
     * @return a cron formatted string e.g. "0 0 0 * * *" which is supposed to specify how often the cache is to be reloaded
     */
    String getCron();

    /**
     * This method is the trigger which should be invoked in order to load/reload the cache
     * */
    void load();
}
