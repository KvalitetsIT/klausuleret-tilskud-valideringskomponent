package dk.kvalitetsit.itukt.common.model;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;

import java.util.Optional;

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
                : Optional.of(new UnspecifiedError());
    }

    private boolean itemMatches(ExistingDrugMedication value) {
        return value instanceof ExistingDrugMedication(String atcCodeValue, String formCodeValue, String routeOfAdministrationCodeValue) &&
                valueMatchesCondition(atcCodeValue, this.atcCode) &&
                valueMatchesCondition(formCodeValue, this.formCode) &&
                valueMatchesCondition(routeOfAdministrationCodeValue, this.routeOfAdministrationCode);
    }

    private boolean valueMatchesCondition(String value, String condition) {
        return condition.equals(value) || condition.equals("*");
    }
}
