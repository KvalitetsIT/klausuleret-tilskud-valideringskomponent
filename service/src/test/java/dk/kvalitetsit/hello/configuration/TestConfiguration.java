package dk.kvalitetsit.hello.configuration;

import dk.kvalitetsit.hello.dao.HelloDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    // Configure beans used for test

    @Bean
    public HelloDaoImpl helloDao(DataSource dataSource) {
        return new HelloDaoImpl(dataSource);
    }



    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
