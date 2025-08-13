package dk.kvalitetsit.itukt.common.configuration;

import dk.kvalitetsit.itukt.common.model.Datasource;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.common")
public record CommonConfiguration(@NotNull Datasource ituktdb) {
}
