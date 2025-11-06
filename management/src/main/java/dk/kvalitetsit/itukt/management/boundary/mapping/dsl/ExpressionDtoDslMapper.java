package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionDtoDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return toDsl(entry).dsl();
    }

    private Dsl toDsl(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> toDsl(b);
            case AgeCondition ageCondition -> toDsl(ageCondition);
            case ExistingDrugMedicationCondition existingDrugMedicationCondition ->
                    toDsl(existingDrugMedicationCondition);
            case IndicationCondition indicationCondition -> toDsl(indicationCondition);
        };
    }

    private Dsl toDsl(IndicationCondition s) {
        return new Dsl(Identifier.INDICATION + " = " + s.getValue(), Dsl.Type.CONDITION);
    }

    private Dsl toDsl(AgeCondition n) {
        return new Dsl(Identifier.AGE + " " + n.getOperator().getValue() + " " + n.getValue(), Dsl.Type.CONDITION);
    }

    private String map(BinaryOperator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }

    private Dsl toDsl(ExistingDrugMedicationCondition expression) {
        return new Dsl(Identifier.EXISTING_DRUG_MEDICATION + " = {" + Identifier.ATC_CODE + " = " + expression.getAtcCode() + ", " + Identifier.FORM_CODE + " = " + expression.getFormCode() + ", " + Identifier.ROUTE + " = " + expression.getRouteOfAdministrationCode() + "}", Dsl.Type.CONDITION);
    }

    private Dsl toDsl(BinaryExpression expression) {
        var orChainedExpressions = getOrChainedExpressions(expression);
        if (!orChainedExpressions.isEmpty()) {
            var mergedConditions = orChainedExpressions.stream()
                    .collect(Collectors.groupingBy(Expression::getClass)).values().stream()
                    .map(this::mergeConditions)
                    .toList();
            String dsl = String.join(" eller ", mergedConditions);
            return new Dsl(dsl, mergedConditions.size() > 1 ? Dsl.Type.OR : Dsl.Type.CONDITION);
        }

        var leftDsl = toDsl(expression.getLeft());
        var rightDsl = toDsl(expression.getRight());
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

        String dsl = leftDslString + " " + map(expression.getOperator()) + " " + rightDslString;
        return new Dsl(dsl, expression.getOperator() == BinaryOperator.OR ? Dsl.Type.OR : Dsl.Type.AND);
    }

    private String parenthesize(String s) {
        return "(" + s + ")";
    }

    private String mergeConditions(List<? extends Expression> conditions) {
        if (conditions.isEmpty()) return "";
        if (conditions.size() == 1) return map(conditions.getFirst());

        return switch (conditions.getFirst()) {
            case AgeCondition ignored ->
                    mergeAgeConditions(conditions.stream().map(c -> (AgeCondition) c).collect(Collectors.toList()));
            case ExistingDrugMedicationCondition ignored ->
                    mergeExistingDrugMedicationConditions(conditions.stream().map(c -> (ExistingDrugMedicationCondition) c).collect(Collectors.toList()));
            case IndicationCondition ignored ->
                    mergeIndicationConditions(conditions.stream().map(c -> (IndicationCondition) c).collect(Collectors.toList()));
            case BinaryExpression ignored ->
                    throw new IllegalStateException("BinaryExpression cannot be a chained expression");
        };
    }

    private String mergeAgeConditions(List<AgeCondition> ageConditions) {
        return ageConditions.stream()
                .collect(Collectors.groupingBy(AgeCondition::getOperator))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Operator operator = entry.getKey();
                    List<AgeCondition> conditions = entry.getValue();
                    if (operator == Operator.EQUAL) {
                        return mergeAgeEqualsConditions(conditions);
                    } else {
                        return conditions.stream().map(this::map).collect(Collectors.joining(" eller "));
                    }
                })
                .collect(Collectors.joining(" eller "));

    }

    private String mergeAgeEqualsConditions(List<AgeCondition> ageConditions) {
        if (ageConditions.size() == 1) return map(ageConditions.getFirst());
        return mergeConditions(Identifier.AGE, ageConditions, e -> Integer.toString(e.getValue()));
    }

    private String mergeIndicationConditions(List<IndicationCondition> indicationConditions) {
        return mergeConditions(Identifier.INDICATION, indicationConditions, IndicationCondition::getValue);
    }

    private String mergeExistingDrugMedicationConditions(List<ExistingDrugMedicationCondition> existingDrugMedicationConditions) {
        return mergeConditions(
                Identifier.EXISTING_DRUG_MEDICATION,
                existingDrugMedicationConditions,
                (e) -> String.format(
                        "{ATC = %s, FORM = %s, ROUTE = %s}",
                        e.getAtcCode(),
                        e.getFormCode(),
                        e.getRouteOfAdministrationCode()
                )
        );
    }

    private <T extends Expression> String mergeConditions(Identifier identifier, List<T> conditions, Function<T, String> supplier) {
        String joined = conditions.stream()
                .map(supplier)
                .collect(Collectors.joining(", "));
        return identifier + " i [" + joined + "]";
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
        return Stream.concat(leftChainedExpressions.stream(), rightChainedExpressions.stream())
                .collect(Collectors.toList());
    }

    private record Dsl(String dsl, Type type) {
        private enum Type {
            CONDITION,
            OR,
            AND,
        }
    }
}
