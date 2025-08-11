package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.Datasource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "management")
public record ManagementConfiguration(Datasource jdbc) {
}
