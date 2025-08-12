package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.common.model.DatasourceConfiguration;
import org.springframework.validation.annotation.Validated;

@Validated
public record ManagementConfiguration(DatasourceConfiguration jdbc) {
}
