package dk.kvalitetsit.itukt.common.configuration;

import jakarta.annotation.Nullable;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

public record ConnectionConfiguration(
        @Nullable String testQuery,
        @DefaultValue("PT30M") Duration maxAge,
        @Nullable Duration maxIdleTime) {
}
