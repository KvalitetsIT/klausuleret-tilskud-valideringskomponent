package dk.kvalitetsit.itukt.common.configuration;

import dk.kvalitetsit.itukt.common.model.DatasourceConfiguration;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "itukt.common")
public record CommonConfiguration(List<String> allowedOrigins,
                                  @NotNull DatasourceConfiguration ituktdb) {
    public CommonConfiguration {
        if (allowedOrigins == null) {
            allowedOrigins = List.of("http://localhost");
        }
    }
}
