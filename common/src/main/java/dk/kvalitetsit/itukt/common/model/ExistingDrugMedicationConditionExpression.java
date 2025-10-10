package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

public record ExistingDrugMedicationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        Optional<Optional<ValidationFailed>> validationFailed = validationInput.existingDrugMedication()
                .map(medicationList -> medicationList.stream().anyMatch(this::itemMatches)
                        ? Optional.empty()
                        : Optional.of(new ExistingDrugMedicationError(atcCode, formCode, routeOfAdministrationCode)));
        return validationFailed.orElse(Optional.of(new ExistingDrugMedicationRequired()));
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