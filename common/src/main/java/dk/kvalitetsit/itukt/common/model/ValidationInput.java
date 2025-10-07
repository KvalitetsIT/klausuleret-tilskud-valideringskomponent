package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

public record ValidationInput(
        String personId,
        String createdById,
        Optional<String> reportedById,
        List<Integer> skippedErrorCodes,
        int citizenAge,
        long drugId,
        String indicationCode,
        Optional<List<ExistingDrugMedication>> existingDrugMedication) {

    public Object getByField(Expression.Condition.Field field) {
        return switch (field) {
            case AGE -> citizenAge;
            case INDICATION -> indicationCode;
            case EXISTING_DRUG_MEDICATION -> existingDrugMedication;
        };
    }
}
