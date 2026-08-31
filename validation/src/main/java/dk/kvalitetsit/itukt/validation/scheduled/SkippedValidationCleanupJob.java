package dk.kvalitetsit.itukt.validation.scheduled;

import dk.kvalitetsit.itukt.common.configuration.CleanupJobConfiguration;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkippedValidationCleanupJob implements ScheduledJob {
    private final Logger logger = LoggerFactory.getLogger(SkippedValidationCleanupJob.class);
    private final CleanupJobConfiguration cleanupJobConfiguration;
    private final SkippedValidationRepository skippedValidationRepository;

    public SkippedValidationCleanupJob(CleanupJobConfiguration cleanupJobConfiguration, SkippedValidationRepository skippedValidationRepository) {
        this.cleanupJobConfiguration = cleanupJobConfiguration;
        this.skippedValidationRepository = skippedValidationRepository;
    }

    @Override
    public String getCron() {
        return cleanupJobConfiguration.cron();
    }

    @Override
    public void run() {
        long deletionCount = skippedValidationRepository.deleteOlderThan(cleanupJobConfiguration.retentionPeriod());
        logger.info("Deleted {} skipped validations older than {}", deletionCount, cleanupJobConfiguration.retentionPeriod());
    }
}
