package dk.kvalitetsit.itukt.common.configuration;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
public record ConnectionConfiguration(
        String testQuery,
        Duration maxAge,
        Duration maxIdleTime
) {
    public ConnectionConfiguration {
        if (maxAge == null) {
            maxAge = Duration.parse("PT30M");
        }
    }
}
