package dk.kvalitetsit.klaus;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "validation")
public class ValidationConfiguration {
}
