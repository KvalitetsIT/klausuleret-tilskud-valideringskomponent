package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public record DatasourceConfiguration(
        @NotBlank String url,
        @NotBlank String username,
        @NotNull String password,
        @NotNull @Valid ConnectionConfiguration connection
) {
    public DatasourceConfiguration {
        if (connection == null) {
            connection = new ConnectionConfiguration(null, Duration.parse("PT30M"), null);
        }
    }
}