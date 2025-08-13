package dk.kvalitetsit.itukt.configuration;


import dk.kvalitetsit.itukt.management.ManagementConfiguration;
import dk.kvalitetsit.itukt.validation.ValidationConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Main configuration settings for the application.
 */
@Validated
@ConfigurationProperties(prefix = "app")
@EnableTransactionManagement
public record MainConfiguration(
        List<String> allowedOrigins,
        ValidationConfiguration validation,
        ManagementConfiguration management
) {
    public MainConfiguration {
        if (allowedOrigins == null) {
            allowedOrigins = List.of("http://localhost");
        }
    }

}
