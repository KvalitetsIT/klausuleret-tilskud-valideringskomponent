package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.management")
public record ManagementConfiguration(@NotNull @Valid ClauseConfiguration clause) {
    public record ClauseConfiguration(@NotNull CacheConfiguration cache) {
    }
}