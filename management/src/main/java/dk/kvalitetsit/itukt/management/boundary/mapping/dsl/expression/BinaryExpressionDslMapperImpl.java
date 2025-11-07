package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryExpressionDslMapperImpl implements ExpressionDslMapper<BinaryExpression> {

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
    public String merge(List<BinaryExpression> expressions) {
        throw new IllegalStateException("BinaryExpression cannot be a chained expression");
    }

    @Override
    public Dsl map(BinaryExpression expression) {
        var orChainedExpressions = getOrChainedExpressions(expression);
        if (!orChainedExpressions.isEmpty()) {
            var mergedConditions = orChainedExpressions.stream()
                    .collect(Collectors.groupingBy(Expression::getClass)).values().stream()
                    .map(parent::mergeConditions)
                    .toList();
            String dsl = String.join(" eller ", mergedConditions);
            return new Dsl(dsl, mergedConditions.size() > 1 ? Dsl.Type.OR : Dsl.Type.CONDITION);
        }

        var leftDsl = parent.toDsl(expression.getLeft());
        var rightDsl = parent.toDsl(expression.getRight());
        String leftDslString = leftDsl.dsl();
        String rightDslString = rightDsl.dsl();

        // Parenthesize OR expressions if current operator is AND
        if (expression.getOperator() == BinaryOperator.AND) {
            if (leftDsl.type() == Dsl.Type.OR) {
                leftDslString = parenthesize(leftDslString);
            }
            if (rightDsl.type() == Dsl.Type.OR) {
                rightDslString = parenthesize(rightDslString);
            }
        }

        String dsl = leftDslString + " " + operatorMapper.map(expression.getOperator()) + " " + rightDslString;
        return new Dsl(dsl, expression.getOperator() == BinaryOperator.OR ? Dsl.Type.OR : Dsl.Type.AND);
    }

    private List<Expression> getOrChainedExpressions(Expression expr) {
        return switch (expr) {
            case AgeCondition a -> List.of(a);
            case ExistingDrugMedicationCondition e -> List.of(e);
            case IndicationCondition i -> List.of(i);
            case BinaryExpression b -> getOrChainedExpressions(b);
        };
    }

    private List<Expression> getOrChainedExpressions(BinaryExpression expr) {
        if (expr.getOperator() != BinaryOperator.OR) return List.of();
        var leftChainedExpressions = getOrChainedExpressions(expr.getLeft());
        var rightChainedExpressions = getOrChainedExpressions(expr.getRight());
        if (leftChainedExpressions.isEmpty() || rightChainedExpressions.isEmpty()) {
            return List.of();
        }
        return Stream.concat(leftChainedExpressions.stream(), rightChainedExpressions.stream()).toList();
    }


}
