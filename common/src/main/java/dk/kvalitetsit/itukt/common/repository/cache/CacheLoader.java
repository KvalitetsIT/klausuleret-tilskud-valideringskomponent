package dk.kvalitetsit.itukt.common.repository.cache;

public interface CacheLoader {
    String get_cron();
    void load();
}
