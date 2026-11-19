package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.ExistingDrugMedicationCondition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExistingDrugMedicationExpressionDslMapperImpl implements ExpressionDslMapper<ExistingDrugMedicationCondition> {
    private final StringToDslValueMapper stringToDslValueMapper;

    ExistingDrugMedicationExpressionDslMapperImpl(StringToDslValueMapper stringToDslValueMapper) {
        this.stringToDslValueMapper = stringToDslValueMapper;
    }

    @Override
    public String merge(List<ExistingDrugMedicationCondition> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(
                Identifier.EXISTING_DRUG_MEDICATION,
                expressions,
                this::toString
        );
    }

    @Override
    public Dsl map(ExistingDrugMedicationCondition expression) {
        return new Dsl(
                String.format("%s = %s", Identifier.EXISTING_DRUG_MEDICATION, toString(expression)),
                Dsl.Type.CONDITION
        );
    }

    private String toString(ExistingDrugMedicationCondition expression) {
        String conditions = Stream.of(
                        toString(Identifier.ATC_CODE, expression.getAtcCode()),
                        toString(Identifier.FORM_CODE, expression.getFormCode()),
                        toString(Identifier.ROUTE, expression.getRouteOfAdministrationCode())
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));

        return "{" + conditions + "}";
    }

    private Optional<String> toString(Identifier identifier, String value) {
        if (ExistingDrugMedicationConditionExpression.WILDCARD.equals(value)) {
            return Optional.empty();
        }
        return Optional.of("%s = %s".formatted(identifier, stringToDslValueMapper.map(value)));
    }
}
