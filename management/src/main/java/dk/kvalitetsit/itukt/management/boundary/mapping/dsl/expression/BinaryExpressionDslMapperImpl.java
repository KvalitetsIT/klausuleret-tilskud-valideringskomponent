package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BinaryExpressionDslMapperImpl implements Mapper<BinaryExpression, Dsl> {

    private final ExpressionDtoDslMapper parent;
    private final Mapper<BinaryOperator, String> operatorMapper = entry -> switch (entry) {
        case AND -> "og";
        case OR -> "eller";
    };

    public BinaryExpressionDslMapperImpl(ExpressionDtoDslMapper parent) {
        this.parent = parent;
    }

    private static String parenthesize(String s) {
        return "(" + s + ")";
    }

    @Override
    public Dsl map(BinaryExpression expression) {
        var orChainedExpressions = getOrChainedExpressions(expression);
        return orChainedExpressions.isEmpty() ? mapUnchainedExpressions(expression) : mapChainedExpressions(orChainedExpressions);
    }

    private Dsl mapUnchainedExpressions(BinaryExpression expression) {
        var leftDsl = parent.toDsl(expression.getLeft());
        var rightDsl = parent.toDsl(expression.getRight());

        String dsl = String.format(
                "%s %s %s",
                getDslString(expression.getOperator(), leftDsl),
                operatorMapper.map(expression.getOperator()),
                getDslString(expression.getOperator(), rightDsl)
        );

        return new Dsl(dsl, expression.getOperator() == BinaryOperator.OR ? Dsl.Type.OR : Dsl.Type.AND);
    }

    private String getDslString(@NotNull @Valid BinaryOperator operator, Dsl dsl) {
        boolean isExpectingParenthesis = operator == BinaryOperator.AND && dsl.type() == Dsl.Type.OR;
        return isExpectingParenthesis ? parenthesize(dsl.dsl()) : dsl.dsl();
    }

    private Dsl mapChainedExpressions(List<Expression> orChainedExpressions) {
        var mergedConditions = orChainedExpressions.stream()
                .collect(Collectors.groupingBy(Expression::getClass, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(parent::mergeConditions)
                .toList();
        String dsl = String.join(" eller ", mergedConditions);
        return new Dsl(dsl, mergedConditions.size() > 1 ? Dsl.Type.OR : Dsl.Type.CONDITION);
    }

    private List<Expression> getOrChainedExpressions(Expression expr) {
        if (expr instanceof BinaryExpression b) return getOrChainedExpressions(b);
        else return List.of(expr);
    }

    private List<Expression> getOrChainedExpressions(BinaryExpression expr) {
        if (expr.getOperator() != BinaryOperator.OR) return List.of();
        var leftChainedExpressions = getOrChainedExpressions(expr.getLeft());
        var rightChainedExpressions = getOrChainedExpressions(expr.getRight());
        if (leftChainedExpressions.isEmpty() || rightChainedExpressions.isEmpty()) return List.of();
        return Stream.concat(leftChainedExpressions.stream(), rightChainedExpressions.stream()).toList();
    }


}
