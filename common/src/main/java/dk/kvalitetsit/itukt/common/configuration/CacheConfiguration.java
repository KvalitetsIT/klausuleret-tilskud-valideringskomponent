package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.constraints.NotNull;

public record CacheConfiguration(@NotNull String cron) {
}
