package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.model.Datasource;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "stamdata")
public record StamdataConfiguration(@NotNull Datasource stamdatadb) {
}
