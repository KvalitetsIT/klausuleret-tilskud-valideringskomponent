package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.common")
public record CommonConfiguration(@NotNull @Valid DatasourceConfiguration ituktdb) {
}
