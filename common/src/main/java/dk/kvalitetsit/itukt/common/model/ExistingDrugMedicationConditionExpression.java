package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

public record ExistingDrugMedicationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return validationInput.existingDrugMedication()
                .map(this::validate)
                .orElse(Optional.of(new ExistingDrugMedicationRequired()));
    }

    private Optional<ValidationFailed> validate(List<ExistingDrugMedication> existingDrugMedications) {
        return existingDrugMedications.stream().anyMatch(this::itemMatches)
                ? Optional.empty()
                : Optional.of(new ExistingDrugMedicationError(atcCode, formCode, routeOfAdministrationCode));
    }

    private boolean itemMatches(ExistingDrugMedication value) {
        return valueMatchesCondition(value.atcCode(), this.atcCode) &&
                valueMatchesCondition(value.formCode(), this.formCode) &&
                valueMatchesCondition(value.routeOfAdministrationCode(), this.routeOfAdministrationCode);
    }

    private boolean valueMatchesCondition(String value, String condition) {
        return condition.equals(value) || condition.equals("*");
    }
}