package dk.kvalitetsit.itukt.configuration;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties
public class JacksonConfiguration {
    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}
