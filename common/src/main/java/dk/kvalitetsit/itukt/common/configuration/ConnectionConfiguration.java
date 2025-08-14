package dk.kvalitetsit.itukt.common.configuration;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public record ConnectionConfiguration(
        @Nullable String testQuery,
        @NotNull Duration maxAge,
        @Nullable Duration maxIdleTime
) {
    public ConnectionConfiguration {
        if (maxAge == null) {
            maxAge = Duration.parse("PT30M");
        }
    }
}
