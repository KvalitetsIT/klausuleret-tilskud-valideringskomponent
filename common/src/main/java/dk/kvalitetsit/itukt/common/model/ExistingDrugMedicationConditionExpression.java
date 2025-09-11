package dk.kvalitetsit.itukt.common.model;

import java.util.Collection;

public record ExistingDrugMedicationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public Field field() {
        return Field.EXISTING_DRUG_MEDICATION;
    }

    @Override
    public boolean matches(Object value) {
        return value instanceof Collection<?> valueList && valueList.stream().anyMatch(this::itemMatches);
    }

    private boolean itemMatches(Object value) {
        return value instanceof ExistingDrugMedication(String atcCodeValue, String formCodeValue, String routeOfAdministrationCodeValue) &&
                valueMatchesCondition(atcCodeValue, this.atcCode) &&
                valueMatchesCondition(formCodeValue, this.formCode) &&
                valueMatchesCondition(routeOfAdministrationCodeValue, this.routeOfAdministrationCode);
    }

    private boolean valueMatchesCondition(String value, String condition) {
        return condition.equals(value) || condition.equals("*");
    }
}
