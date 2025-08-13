package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.model.DatasourceConfiguration;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "stamdata")
public record StamdataConfiguration(@NotNull DatasourceConfiguration stamdatadb) {
}
