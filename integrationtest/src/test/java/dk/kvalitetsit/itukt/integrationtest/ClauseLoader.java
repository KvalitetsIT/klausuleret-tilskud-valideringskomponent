package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND;

@TestConfiguration
public class ClauseLoader {

    @Bean
    @Order(1)
    public ApplicationRunner loader(ClauseRepository<Clause> repository) {
        return args -> bootstrapClauses(repository);
    }

    private void bootstrapClauses(ClauseRepository<Clause> repository) {
        System.out.println("Bootstrapping clauses");
        // Hardcoded clause for phase 1
        var expression = new BinaryExpression(
                new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN, 50),
                AND,
                new NumberConditionExpression(Expression.Condition.Field.INDICATION, Operator.EQUAL, 313)
        );

        var clause = new Clause("KRINI", UUID.randomUUID(), expression);

        repository.create(clause);
    }


}
