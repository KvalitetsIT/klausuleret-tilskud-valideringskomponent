package dk.kvalitetsit.klaus.configuration;


import dk.kvalitetsit.klaus.ManagementConfiguration;
import dk.kvalitetsit.klaus.ValidationConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
@EnableTransactionManagement
public record MainConfiguration(
        ValidationConfiguration validation,
        ManagementConfiguration management
) { }
