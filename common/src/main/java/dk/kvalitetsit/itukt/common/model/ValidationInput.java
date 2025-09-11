package dk.kvalitetsit.itukt.common.model;

public record ValidationInput(
        int citizenAge,
        long drugId,
        String indicationCode) {

    public Object getByField(Expression.Condition.Field field) {
        return switch (field) {
            case AGE -> citizenAge;
            case INDICATION -> indicationCode;
            case EXISTING_DRUG_MEDICATION -> null;
        };
    }
}
