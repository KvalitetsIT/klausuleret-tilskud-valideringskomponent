package dk.kvalitetsit.itukt.common.model;

import java.util.List;

public record ValidationInput(
        int citizenAge,
        long drugId,
        String indicationCode,
        List<ExistingDrugMedication> existingDrugMedication) {

    public Object getByField(Expression.Condition.Field field) {
        return switch (field) {
            case AGE -> citizenAge;
            case INDICATION -> indicationCode;
            case EXISTING_DRUG_MEDICATION -> existingDrugMedication;
        };
    }
}
