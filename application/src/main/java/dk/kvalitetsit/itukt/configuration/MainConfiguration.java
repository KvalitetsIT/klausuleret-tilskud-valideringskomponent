package dk.kvalitetsit.itukt.configuration;


import dk.kvalitetsit.itukt.ManagementConfiguration;
import dk.kvalitetsit.itukt.ValidationConfiguration;
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
