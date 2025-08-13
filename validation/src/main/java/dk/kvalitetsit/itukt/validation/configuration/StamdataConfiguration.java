package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DatasourceConfiguration;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record StamdataConfiguration(@NotNull DatasourceConfiguration stamdatadb) {
}
