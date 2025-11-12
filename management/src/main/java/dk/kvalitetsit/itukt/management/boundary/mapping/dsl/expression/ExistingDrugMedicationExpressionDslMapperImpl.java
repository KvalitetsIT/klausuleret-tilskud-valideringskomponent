package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.ExistingDrugMedicationCondition;

import java.util.List;

class ExistingDrugMedicationExpressionDslMapperImpl implements ExpressionDslMapper<ExistingDrugMedicationCondition> {

    private static String toString(ExistingDrugMedicationCondition expression) {
        return String.format("{%s = %s, %s = %s, %s = %s}",
                Identifier.ATC_CODE,
                expression.getAtcCode(),
                Identifier.FORM_CODE,
                expression.getFormCode(),
                Identifier.ROUTE,
                expression.getRouteOfAdministrationCode()
        );
    }

    @Override
    public String merge(List<ExistingDrugMedicationCondition> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(
                Identifier.EXISTING_DRUG_MEDICATION,
                expressions,
                ExistingDrugMedicationExpressionDslMapperImpl::toString
        );
    }

    @Override
    public Dsl map(ExistingDrugMedicationCondition expression) {
        return new Dsl(
                String.format("%s = %s", Identifier.EXISTING_DRUG_MEDICATION, toString(expression)),
                Dsl.Type.CONDITION
        );
    }
}
