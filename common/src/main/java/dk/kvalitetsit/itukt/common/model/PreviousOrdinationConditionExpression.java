package dk.kvalitetsit.itukt.common.model;

public record PreviousOrdinationConditionExpression(
        String atcCode,
        String formCode,
        String routeOfAdministrationCode) implements Expression.Condition {

    @Override
    public Field field() {
        return Field.PREVIOUS_ORDINATION;
    }

    @Override
    public boolean matches(Object value) {
        return false;
    }
}
