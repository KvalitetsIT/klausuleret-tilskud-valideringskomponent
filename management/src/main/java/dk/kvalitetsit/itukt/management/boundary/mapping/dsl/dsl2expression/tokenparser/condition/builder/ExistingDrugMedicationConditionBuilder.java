package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.Operator;

import java.util.Map;
import java.util.Set;

public class ExistingDrugMedicationConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.EXISTING_DRUG_MEDICATION;
    }

    @Override
    public ExistingDrugMedicationCondition build(Operator operator, Condition.Value value) {
        if (operator != Operator.EQUAL) {
            throw new DslParserException("Unsupported operator for existing drug medication condition: " + operator);
        }
        Map<String, String> values = value.asStructured().values();
        Set<String> validValueKeys = Set.of(Identifier.ATC_CODE.toString(), Identifier.FORM_CODE.toString(), Identifier.ROUTE.toString());
        if (!validValueKeys.containsAll(values.keySet())) {
            throw new DslParserException("Existing drug medication condition must only contain values for: " + String.join(", ", validValueKeys));
        }

        return new ExistingDrugMedicationCondition(
                values.getOrDefault(Identifier.ATC_CODE.toString(), "*"),
                values.getOrDefault(Identifier.FORM_CODE.toString(), "*"),
                values.getOrDefault(Identifier.ROUTE.toString(), "*"),
                ExpressionType.EXISTING_DRUG_MEDICATION
        );
    }
}
