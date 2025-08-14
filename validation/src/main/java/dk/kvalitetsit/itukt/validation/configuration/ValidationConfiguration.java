package dk.kvalitetsit.itukt.validation.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.validation")
public record ValidationConfiguration(@NotNull @Valid StamdataConfiguration stamdata) {

}
