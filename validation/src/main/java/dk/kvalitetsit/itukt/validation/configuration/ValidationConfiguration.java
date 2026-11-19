package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.CleanupJobConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "itukt.validation")
public record ValidationConfiguration(
        @NotNull @Valid StamdataConfiguration stamdata,
        @NotNull @Valid ValidationConfiguration.SkippedValidationConfiguration skippedValidation
) {
    public record SkippedValidationConfiguration(
            @NotNull @Valid CleanupJobConfiguration cleanupJob
    ) {
    }
}
