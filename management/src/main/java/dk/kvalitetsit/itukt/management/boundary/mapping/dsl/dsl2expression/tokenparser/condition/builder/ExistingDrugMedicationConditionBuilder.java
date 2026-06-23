package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedExistingDrugMedicationKeysException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.Operator;

import java.util.List;
import java.util.Map;

public class ExistingDrugMedicationConditionBuilder implements ConditionBuilder {
    private static final List<String> IDENTIFIERS = List.of(
            Identifier.ATC_CODE.toString(),
            Identifier.FORM_CODE.toString(),
            Identifier.ROUTE.toString());

    @Override
    public Identifier identifier() {
        return Identifier.EXISTING_DRUG_MEDICATION;
    }

    @Override
    public ExistingDrugMedicationCondition build(Operator operator, Condition.Value value) {
        if (operator != Operator.EQUAL) {
            throw new UnexpectedValueException(operator.getValue());
        }
        Map<String, String> values = value.asStructured().values();
        if (!IDENTIFIERS.containsAll(values.keySet())) {
            throw new UnexpectedExistingDrugMedicationKeysException(IDENTIFIERS);
        }

        return new ExistingDrugMedicationCondition(
                values.getOrDefault(Identifier.ATC_CODE.toString(), "*"),
                values.getOrDefault(Identifier.FORM_CODE.toString(), "*"),
                values.getOrDefault(Identifier.ROUTE.toString(), "*"),
                ExpressionType.EXISTING_DRUG_MEDICATION
        );
    }
}
