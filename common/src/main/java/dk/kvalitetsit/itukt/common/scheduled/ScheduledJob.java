package dk.kvalitetsit.itukt.common.scheduled;

/**
 * A simple facade which is exposed towards the {@link JobScheduler}.
 * The purpose of the interface is to abstract away additional concerns which is not supposed to be accessible by the {@link JobScheduler}
 */
public interface ScheduledJob {
    /**
     * @return a cron formatted string e.g. "0 0 0 * * *" which is supposed to specify how often the job should be executed
     */
    String getCron();

    /**
     * This method is the trigger for the job
     * */
    void run();
}
