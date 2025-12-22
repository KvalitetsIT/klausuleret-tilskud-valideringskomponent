package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.ExistingDrugMedicationError;
import static dk.kvalitetsit.itukt.common.model.ValidationError.ExistingDrugMedicationRequired;


/**
 * An expression representing a condition that checks for the presence of a specific
 * existing drug medication in a {@link ValidationInput}.
 * <p>
 * The condition is satisfied if there is at least one {@link ExistingDrugMedication}
 * in the input that matches the required ATC code, form code, and route of administration code.
 * Wildcards ("*") in any field will match any value.
 *
 */
public record ExistingDrugMedicationConditionExpression(ExistingDrugMedication existingDrugMedication) implements Expression.Condition {

    /**
     * Validates the existing drug medication condition against the provided {@link ValidationInput}.
     * <p>
     * If the input contains a list of existing medications, the method checks if any medication
     * matches the specified ATC code, form code, and route of administration code (allowing wildcards).
     * If no matching medication is found, or if the list is missing, an appropriate validation failure
     * is returned.
     *
     * @param validationInput the input containing the existing medications to validate
     * @return an empty {@code Optional} if the condition is satisfied; otherwise, an {@code Optional} containing a validation failure
     */
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