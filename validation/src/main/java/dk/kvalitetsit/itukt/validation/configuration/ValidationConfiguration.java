package dk.kvalitetsit.itukt.validation.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.validation")
public record ValidationConfiguration(@NotNull StamdataConfiguration stamdata) {

}
