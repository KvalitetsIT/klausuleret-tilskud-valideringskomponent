package dk.kvalitetsit.itukt;

import dk.kvalitetsit.itukt.model.Datasource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "validation")
public record ValidationConfiguration(Datasource jdbc) {

}
