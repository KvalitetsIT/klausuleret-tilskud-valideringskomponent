package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.configuration.DatasourceConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StamdataConfiguration(
        @NotNull @Valid CacheConfiguration cache,
        @NotNull @Valid DatasourceConfiguration stamdatadb
) { }
