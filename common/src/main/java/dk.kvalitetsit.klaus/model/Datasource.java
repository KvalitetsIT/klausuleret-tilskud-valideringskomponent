package dk.kvalitetsit.klaus.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record Datasource(@NotBlank String url, @NotBlank String username, @NotBlank String password) {}