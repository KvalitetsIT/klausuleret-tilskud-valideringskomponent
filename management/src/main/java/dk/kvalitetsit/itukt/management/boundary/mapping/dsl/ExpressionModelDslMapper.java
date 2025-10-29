package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionModelDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> map(b);
            case IndicationConditionExpression s -> map(s);
            case AgeConditionExpression n -> map(n);
            case ExistingDrugMedicationConditionExpression e -> map(e);
        };
    }

    private String map(IndicationConditionExpression s) {
        return Identifier.INDICATION + " = " + s.requiredValue();
    }

    private String map(AgeConditionExpression n) {
        return Identifier.AGE + " " + n.operator().getValue() + " " + n.value();
    }

    private String map(BinaryExpression.Operator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }

    private String map(ExistingDrugMedicationConditionExpression expression) {
        return Identifier.EXISTING_DRUG_MEDICATION + " = {" + Identifier.ATC_CODE + " = " + expression.atcCode() + ", " + Identifier.FORM_CODE + " = " + expression.formCode() + ", " + Identifier.ROUTE + " = " + expression.routeOfAdministrationCode() + "}";
    }

    private String map(BinaryExpression expression) {
        if (isChain(expression, ExistingDrugMedicationConditionExpression.class)) return mergeExistingDrugMedicationConditions(expression);
        if (isChain(expression, AgeConditionExpression.class)) return mergeAgeConditions(expression);
        if (isChain(expression, IndicationConditionExpression.class)) return mergeIndicationConditions(expression);


        return map(expression.left()) + " " + map(expression.operator()) + " " + map(expression.right());
    }

    private String mergeAgeConditions(BinaryExpression expression) {
        return merge(
                AgeConditionExpression.class,
                expression,
                e -> Integer.toString(e.value()),
                Identifier.AGE
        );

    }

    private String mergeExistingDrugMedicationConditions(BinaryExpression expression) {
        return merge(
                ExistingDrugMedicationConditionExpression.class,
                expression,
                (e) -> String.format(
                        "{ATC = %s, FORM = %s, ROUTE = %s}",
                        e.atcCode(),
                        e.formCode(),
                        e.routeOfAdministrationCode()
                ),
                Identifier.EXISTING_DRUG_MEDICATION
        );
    }

    private String mergeIndicationConditions(BinaryExpression expression) {
        return merge(
                IndicationConditionExpression.class,
                expression,
                IndicationConditionExpression::requiredValue,
                Identifier.INDICATION
        );
    }


    private <T extends Expression.Condition> String merge(Class<T> conditionClass, BinaryExpression expression, Function<T, String> conditionToString, Identifier field) {
        List<T> collected = collect(expression, conditionClass);
        String joined = collected.stream()
                .map(conditionToString)
                .collect(Collectors.joining(", "));
        return field + " i [" + joined + "]";
    }

    private <T extends Expression> boolean isChain(Expression expr, Class<T> clazz) {
        if (clazz.isInstance(expr)) return true;
        if (expr instanceof BinaryExpression(Expression left, BinaryExpression.Operator operator, Expression right)
                && operator == BinaryExpression.Operator.OR) {
            return isChain(left, clazz) && isChain(right, clazz);
        }
        return false;
    }

    private <T extends Expression> List<T> collect(Expression expr, Class<T> clazz) {
        if (clazz.isInstance(expr)) {
            return List.of(clazz.cast(expr));
        }
        if (expr instanceof BinaryExpression(Expression left, BinaryExpression.Operator operator, Expression right)
                && operator == BinaryExpression.Operator.OR) {
            return Stream.concat(
                            collect(left, clazz).stream(),
                            collect(right, clazz).stream())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

