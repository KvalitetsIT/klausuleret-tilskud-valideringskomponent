package dk.kvalitetsit.itukt.validation;

import dk.kvalitetsit.itukt.common.model.DatasourceConfiguration;
import org.springframework.validation.annotation.Validated;

@Validated
public record ValidationConfiguration(DatasourceConfiguration jdbc) {
}
