package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "itukt.common")
public record CommonConfiguration(@DefaultValue("http://localhost") List<String> allowedOrigins,
                                  @NotNull @Valid DatasourceConfiguration ituktdb) {
}
