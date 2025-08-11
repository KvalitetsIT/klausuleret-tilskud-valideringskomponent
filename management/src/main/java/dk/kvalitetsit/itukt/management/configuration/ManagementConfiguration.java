package dk.kvalitetsit.itukt.management.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.management")
public record ManagementConfiguration() {
}
