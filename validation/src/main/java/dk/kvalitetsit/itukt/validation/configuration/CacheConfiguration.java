package dk.kvalitetsit.itukt.validation.configuration;

import jakarta.validation.constraints.NotNull;

public record CacheConfiguration(@NotNull String cron) {
}
