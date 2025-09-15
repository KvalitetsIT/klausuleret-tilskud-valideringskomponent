package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ClauseField;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.UUID;

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
        var expression = new Expression.BinaryExpression(
                new Expression.Condition(ClauseField.AGE.name(), Operator.GREATER_THAN, List.of("50")),
                Expression.BinaryExpression.BinaryOperator.AND,
                new Expression.Condition(ClauseField.INDICATION.name(), Operator.EQUAL, List.of("313"))
        );

        var clause = new Clause("KRINI", UUID.randomUUID(), expression);

        repository.create(clause);
    }


}
