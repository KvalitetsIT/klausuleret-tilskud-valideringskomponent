package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.Operator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgeConditionDslMapperImpl implements ExpressionDslMapper<AgeCondition> {
    private final ExpressionDtoDslMapper parent;

    public AgeConditionDslMapperImpl(ExpressionDtoDslMapper parent) {
        this.parent = parent;
    }

    @Override
    public String merge(List<AgeCondition> ageConditions) {
        return ageConditions.stream()
                .collect(Collectors.groupingBy(AgeCondition::getOperator))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(this::toString)
                .collect(Collectors.joining(" eller "));
    }

    private String toString(Map.Entry<@NotNull @Valid Operator, @NotNull List<AgeCondition>> entry) {
        Operator operator = entry.getKey();
        List<AgeCondition> conditions = entry.getValue();
        if (operator == Operator.EQUAL) return mergeAgeEqualsConditions(conditions);
        return conditions.stream().map(parent::map).collect(Collectors.joining(" eller "));
    }

    @Override
    public Dsl map(AgeCondition entry) {
        return new Dsl(Identifier.AGE + " " + entry.getOperator().getValue() + " " + entry.getValue(), Dsl.Type.CONDITION);
    }

    private String mergeAgeEqualsConditions(List<AgeCondition> ageConditions) {
        if (ageConditions.size() == 1) return parent.map(ageConditions.getFirst());
        return ExpressionDtoDslMapper.mergeConditions(Identifier.AGE, ageConditions, e -> Integer.toString(e.getValue()));
    }

}
