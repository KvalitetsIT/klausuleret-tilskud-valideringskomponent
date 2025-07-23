package dk.kvalitetsit.klaus.configuration;


import dk.kvalitetsit.klaus.repository.ClauseDao;
import dk.kvalitetsit.klaus.repository.ClauseDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConfigurationProperties
public record TestConfiguration() {
    // Configure beans used for test

    @Bean
    public ClauseDao helloDao(@Qualifier("validationDataSource") DataSource dataSource) {
        return new ClauseDaoImpl(dataSource);
    }



}
