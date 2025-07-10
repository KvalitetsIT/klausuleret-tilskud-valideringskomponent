package dk.kvalitetsit.klaus.configuration;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties
public class JacksonConfiguration {
    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}
