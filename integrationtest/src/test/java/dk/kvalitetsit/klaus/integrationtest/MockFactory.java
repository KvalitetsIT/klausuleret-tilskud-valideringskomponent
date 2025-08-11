package dk.kvalitetsit.klaus.integrationtest;

import org.openapitools.client.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockFactory {

    public static final String dsl = "Klausul CHOL: (ATC = C10BA03) eller (ATC i C10BA02, C10BA05) og (ALDER >= 13)";


    public static final Expression expressionDto = new BinaryExpression()
            .type("BinaryExpression")
            .operator(BinaryOperator.OR)
            .left(new Condition().type("Condition").field("ATC").operator(Operator.EQUAL).values(List.of("C10BA03")))
            .right(new BinaryExpression().type("BinaryExpression").operator(BinaryOperator.AND)
                    .left(new Condition().type("Condition").field("ATC").operator(Operator.I).values(List.of("C10BA02", "C10BA05")))
                    .right(new Condition().type("Condition").field("ALDER").operator(Operator.GREATER_THAN_OR_EQUAL_TO).values(List.of("13")))
            );

    public static final Clause clauseDto = new Clause().name("CHOL").expression(expressionDto);

    public static final dk.kvalitetsit.itukt.common.model.Expression expressionModel = new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
            new dk.kvalitetsit.itukt.common.model.Expression.Condition("ATC", dk.kvalitetsit.itukt.common.model.Operator.EQUAL, List.of("C10BA03")),
            dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.OR,
            new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition("ATC", dk.kvalitetsit.itukt.common.model.Operator.IN, List.of("C10BA02", "C10BA05")),
                    dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.AND,
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition("ALDER", dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO, List.of("13"))
            ));

    public static final dk.kvalitetsit.itukt.common.model.Clause clauseModel = new dk.kvalitetsit.itukt.common.model.Clause("CHOL", Optional.of(UUID.randomUUID()), expressionModel);
}

