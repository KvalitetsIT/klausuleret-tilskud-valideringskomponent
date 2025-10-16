package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return Field.INDICATION + " = " + s.requiredValue();
    }

    private String map(AgeConditionExpression n) {
        return Field.AGE + " " + n.operator().getValue() + " " + n.value();
    }

    private String map(BinaryExpression.Operator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }

    private String map(ExistingDrugMedicationConditionExpression expression) {
        return "EKSISTERENDE_LÆGEMIDDEL = {ATC = " + expression.atcCode() + ", FORM = " + expression.formCode() + ", ROUTE = " + expression.routeOfAdministrationCode() + "}";
    }

    private String map(BinaryExpression expression) {
        if (isChain(expression, ExistingDrugMedicationConditionExpression.class)) {
            List<ExistingDrugMedicationConditionExpression> collected = collect(expression, ExistingDrugMedicationConditionExpression.class);
            String joined = collected.stream()
                    .map(e -> String.format("{ATC = %s, FORM = %s, ROUTE = %s}",
                            e.atcCode(), e.formCode(), e.routeOfAdministrationCode()))
                    .collect(Collectors.joining(", "));
            return "EKSISTERENDE_LÆGEMIDDEL i [" + joined + "]";
        }

        if (isChain(expression, IndicationConditionExpression.class)) {
            List<IndicationConditionExpression> collected = collect(expression, IndicationConditionExpression.class);
            String joined = collected.stream()
                    .map(IndicationConditionExpression::requiredValue)
                    .collect(Collectors.joining(", "));
            return "INDICATION i [" + joined + "]";
        }

        return map(expression.left()) + " " + map(expression.operator()) + " " + map(expression.right());
    }

    private <T extends Expression> boolean isChain(Expression expr, Class<T> clazz) {
        if (clazz.isInstance(expr)) return true;
        if (expr instanceof BinaryExpression(
                Expression left, BinaryExpression.Operator operator, Expression right
        ) && operator == BinaryExpression.Operator.OR) {
            return isChain(left, clazz) && isChain(right, clazz);
        }
        return false;
    }

    private <T extends Expression> List<T> collect(Expression expr, Class<T> clazz) {
        List<T> acc = new ArrayList<>();
        collect(expr, clazz, acc);
        return acc;
    }

    private <T extends Expression> void collect(Expression expr, Class<T> clazz, List<T> acc) {
        if (clazz.isInstance(expr)) {
            acc.add(clazz.cast(expr));
        } else if (expr instanceof BinaryExpression(
                Expression left, BinaryExpression.Operator operator, Expression right
        ) && operator == BinaryExpression.Operator.OR) {
            collect(left, clazz, acc);
            collect(right, clazz, acc);
        }
    }
}

