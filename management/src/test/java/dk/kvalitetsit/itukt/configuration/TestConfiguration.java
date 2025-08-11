package dk.kvalitetsit.itukt.configuration;


import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConfigurationProperties
public record TestConfiguration() {
    // Configure beans used for test

    @Bean
    public ClauseRepository helloDao(@Qualifier("validationDataSource") DataSource dataSource) {
        return new ClauseRepositoryImpl(dataSource);
    }



}
