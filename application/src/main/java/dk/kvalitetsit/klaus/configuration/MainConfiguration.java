package dk.kvalitetsit.klaus.configuration;


import dk.kvalitetsit.klaus.ClauseConfiguration;
import dk.kvalitetsit.klaus.ValidationConfiguration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
@EnableTransactionManagement
public record MainConfiguration(
        DatasourceConfiguration jdbc,
        ValidationConfiguration validation,
        ClauseConfiguration clauseConfiguration
) {
    record DatasourceConfiguration(@NotNull Datasource master, @NotNull Datasource validation) {
    }

    record Datasource(@NotBlank String url, @NotBlank String username, @NotBlank String password) {
    }
}
