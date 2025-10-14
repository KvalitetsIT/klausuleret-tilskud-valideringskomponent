package dk.kvalitetsit.itukt.common.model;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

public record ExistingDrugMedicationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var existingDrugMedication = validationInput.existingDrugMedication()
                .orElseThrow(ExistingDrugMedicationRequiredException::new);
        return existingDrugMedication.stream().anyMatch(this::itemMatches)
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