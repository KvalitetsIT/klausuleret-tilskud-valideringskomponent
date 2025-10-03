package dk.kvalitetsit.itukt.common.repository.cache;

public interface CacheLoader {
    /**
     * @return a cron formatted string ex. "0 0 0 * * *" which is supposed to specify how often the cache is to be reloaded
     */
    String getCron();

    /** This method is the trigger which should be invoked in order to load/reload the cache */
    void load();
}
