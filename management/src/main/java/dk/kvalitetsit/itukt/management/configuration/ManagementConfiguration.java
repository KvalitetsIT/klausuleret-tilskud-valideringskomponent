package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.management")
public record ManagementConfiguration(CacheConfiguration cache) {
}
