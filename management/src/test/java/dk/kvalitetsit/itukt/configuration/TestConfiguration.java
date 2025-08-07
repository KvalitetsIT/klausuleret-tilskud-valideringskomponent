package dk.kvalitetsit.itukt.configuration;


import dk.kvalitetsit.itukt.repository.ClauseRepository;
import dk.kvalitetsit.itukt.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.repository.model.ClauseEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConfigurationProperties
public record TestConfiguration() {
    // Configure beans used for test

    @Bean
    public ClauseRepository<ClauseEntity> helloDao(@Qualifier("appDataSource") DataSource dataSource) {
        return new ClauseRepositoryImpl(dataSource);
    }



}
