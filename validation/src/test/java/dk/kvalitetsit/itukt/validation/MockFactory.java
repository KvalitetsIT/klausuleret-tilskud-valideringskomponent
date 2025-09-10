package dk.kvalitetsit.itukt.validation;

import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;

import java.util.List;
import java.util.Map;

public class MockFactory {

    public static final dk.kvalitetsit.itukt.common.model.Expression expressionModel = new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
            new dk.kvalitetsit.itukt.common.model.Expression.Condition(
                    "ATC",
                    Operator.EQUAL,
                    "C10BA03"
            ),
            dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.OR,
            new dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression(
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition(
                            "ATC",
                            Operator.EQUAL,
                            "C10BA02"
                    ),
                    dk.kvalitetsit.itukt.common.model.Expression.BinaryExpression.BinaryOperator.AND,
                    new dk.kvalitetsit.itukt.common.model.Expression.Condition(
                            "ALDER",
                            dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO,
                            "13"
                    )
            )
    );

    public static DataContext ctx = new DataContext(Map.of(
            "ALDER", List.of("13"),
            "ATC", List.of("C10BA02", "C10BA05")
    ));
}
