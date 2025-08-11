package dk.kvalitetsit.itukt.validation;

import dk.kvalitetsit.itukt.common.model.Datasource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "validation")
public record ValidationConfiguration(Datasource jdbc) {

}
