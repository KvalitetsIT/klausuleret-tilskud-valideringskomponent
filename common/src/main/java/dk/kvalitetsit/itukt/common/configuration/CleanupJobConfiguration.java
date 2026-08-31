package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.constraints.NotNull;

import java.time.Period;

public record CleanupJobConfiguration(
        @NotNull String cron,
        @NotNull Period retentionPeriod) {
}
