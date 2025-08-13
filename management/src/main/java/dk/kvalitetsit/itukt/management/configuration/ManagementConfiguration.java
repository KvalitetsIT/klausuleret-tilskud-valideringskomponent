package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.model.Datasource;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.management")
public record ManagementConfiguration(@NotNull Datasource jdbc) {
}
