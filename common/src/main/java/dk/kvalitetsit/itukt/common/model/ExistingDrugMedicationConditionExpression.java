package dk.kvalitetsit.itukt.common.model;

import java.util.List;

public record ExistingDrugMedicationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public boolean validates(ValidationInput validationInput) {
        List<ExistingDrugMedication> existingDrugMedication = validationInput.existingDrugMedication();
        return existingDrugMedication.stream().anyMatch(this::itemMatches);
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
