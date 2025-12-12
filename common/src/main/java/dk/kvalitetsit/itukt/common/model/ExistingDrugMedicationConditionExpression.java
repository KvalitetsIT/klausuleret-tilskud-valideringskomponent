package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.ExistingDrugMedicationError;
import static dk.kvalitetsit.itukt.common.model.ValidationError.ExistingDrugMedicationRequired;

public record ExistingDrugMedicationConditionExpression(ExistingDrugMedication existingDrugMedication) implements Expression.Condition {

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return validationInput.existingDrugMedication()
                .map(this::validate)
                .orElse(Optional.of(new ExistingDrugMedicationRequired()));
    }

    private Optional<ValidationFailed> validate(List<ExistingDrugMedication> existingDrugMedications) {
        return existingDrugMedications.stream().anyMatch(this::itemMatches)
                ? Optional.empty()
                : Optional.of(new ExistingDrugMedicationError(existingDrugMedication));
    }

    private boolean itemMatches(ExistingDrugMedication value) {
        return valueMatchesCondition(value.atcCode(), this.existingDrugMedication.atcCode()) &&
                valueMatchesCondition(value.formCode(), this.existingDrugMedication.formCode()) &&
                valueMatchesCondition(value.routeOfAdministrationCode(), this.existingDrugMedication.routeOfAdministrationCode());
    }

    private boolean valueMatchesCondition(String value, String condition) {
        return condition.equals(value) || condition.equals("*");
    }
}