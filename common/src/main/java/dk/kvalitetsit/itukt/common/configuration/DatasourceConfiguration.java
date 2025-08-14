package dk.kvalitetsit.itukt.common.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record DatasourceConfiguration(
        @NotBlank String url,
        @NotBlank String username,
        @NotNull String password,
        @DefaultValue @Valid ConnectionConfiguration connection) {
}