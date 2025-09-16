package dk.kvalitetsit.itukt.common.model;

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
        return false; // TODO: IUAKT-40
    }
}
