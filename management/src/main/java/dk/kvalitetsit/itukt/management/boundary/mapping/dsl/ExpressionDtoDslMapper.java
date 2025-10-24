package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionDtoDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> map(b);
            case AgeCondition ageCondition -> map(ageCondition);
            case ExistingDrugMedicationCondition existingDrugMedicationCondition ->
                    map(existingDrugMedicationCondition);
            case IndicationCondition indicationCondition -> map(indicationCondition);
        };
    }

    private String map(IndicationCondition s) {
        return Identifier.INDICATION + " = " + s.getValue();
    }

    private String map(AgeCondition n) {
        return Identifier.AGE + " " + n.getOperator().getValue() + " " + n.getValue();
    }

    private String map(BinaryOperator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }

    private String map(ExistingDrugMedicationCondition expression) {
        return Identifier.EXISTING_DRUG_MEDICATION + " = {" + Identifier.ATC_CODE + " = " + expression.getAtcCode() + ", " + Identifier.FORM_CODE + " = " + expression.getFormCode() + ", " + Identifier.ROUTE + " = " + expression.getRouteOfAdministrationCode() + "}";
    }

    private String map(BinaryExpression expression) {
        // Handle chains first (merging multiple same-type OR expressions)
        if (isChain(expression, ExistingDrugMedicationCondition.class)) return mergeExistingDrugMedicationConditions(expression);
        if (isChain(expression, AgeCondition.class)) return mergeAgeConditions(expression);
        if (isChain(expression, IndicationCondition.class)) return mergeIndicationConditions(expression);

        String left = map(expression.getLeft());
        String right = map(expression.getRight());

        // Parenthesize OR expressions if current operator is AND
        if (expression.getOperator() == BinaryOperator.AND) {
            if (expression.getLeft() instanceof BinaryExpression leftExpr
                    && leftExpr.getOperator() == BinaryOperator.OR
                    && !isChain(leftExpr, IndicationCondition.class)
                    && !isChain(leftExpr, AgeCondition.class)
                    && !isChain(leftExpr, ExistingDrugMedicationCondition.class)) {
                left = parenthesize(left);
            }
            if (expression.getRight() instanceof BinaryExpression rightExpr
                    && rightExpr.getOperator() == BinaryOperator.OR
                    && !isChain(rightExpr, IndicationCondition.class)
                    && !isChain(rightExpr, AgeCondition.class)
                    && !isChain(rightExpr, ExistingDrugMedicationCondition.class)) {
                right = parenthesize(right);
            }
        }

        return left + " " + map(expression.getOperator()) + " " + right;
    }

    private String parenthesize(String s) {
        return "(" + s + ")";
    }

    private String mergeAgeConditions(BinaryExpression expression) {
        return merge(
                AgeCondition.class,
                expression,
                e -> Integer.toString(e.getValue()),
                Identifier.AGE
        );

    }

    private String mergeExistingDrugMedicationConditions(BinaryExpression expression) {
        return merge(
                ExistingDrugMedicationCondition.class,
                expression,
                (e) -> String.format(
                        "{ATC = %s, FORM = %s, ROUTE = %s}",
                        e.getAtcCode(),
                        e.getFormCode(),
                        e.getRouteOfAdministrationCode()
                ),
                Identifier.EXISTING_DRUG_MEDICATION
        );
    }

    private String mergeIndicationConditions(BinaryExpression expression) {
        return merge(
                IndicationCondition.class,
                expression,
                IndicationCondition::getValue,
                Identifier.INDICATION
        );
    }


    private <T extends Expression> String merge(Class<T> clazz, BinaryExpression expression, Function<T, String> supplier, Identifier identifier) {
        List<T> collected = collect(expression, clazz);
        String joined = collected.stream()
                .map(supplier)
                .collect(Collectors.joining(", "));
        return identifier + " i [" + joined + "]";
    }

    private <T extends Expression> boolean isChain(Expression expr, Class<T> clazz) {
        if (clazz.isInstance(expr)) return true;
        if (expr instanceof BinaryExpression e && e.getOperator() == BinaryOperator.OR) {
            return isChain(e.getLeft(), clazz) && isChain(e.getRight(), clazz);
        }
        return false;
    }

    private <T extends Expression> List<T> collect(Expression expr, Class<T> clazz) {
        if (clazz.isInstance(expr)) {
            return List.of(clazz.cast(expr));
        }
        if (expr instanceof BinaryExpression e
                && e.getOperator() == BinaryOperator.OR) {
            return Stream.concat(
                            collect(e.getLeft(), clazz).stream(),
                            collect(e.getRight(), clazz).stream())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
