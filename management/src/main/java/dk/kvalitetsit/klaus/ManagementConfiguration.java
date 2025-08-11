package dk.kvalitetsit.klaus;

import dk.kvalitetsit.klaus.model.Datasource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "management")
public record ManagementConfiguration(Datasource jdbc) {
}
