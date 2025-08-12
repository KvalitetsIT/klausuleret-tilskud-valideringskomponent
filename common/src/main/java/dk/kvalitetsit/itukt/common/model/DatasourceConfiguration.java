package dk.kvalitetsit.itukt.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
public record DatasourceConfiguration(
        @NotBlank String url,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull ConnectionConfiguration connection
) {
    public DatasourceConfiguration {
        if (connection == null) {
            connection = new ConnectionConfiguration(null, Duration.parse("PT30M"), null);
        }
    }
}