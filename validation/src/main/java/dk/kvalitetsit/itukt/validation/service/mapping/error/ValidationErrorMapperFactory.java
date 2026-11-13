package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

public class ValidationErrorMapperFactory {
    private final StamdataCacheService<Indication> indicationCache;

    public ValidationErrorMapperFactory(StamdataCacheService<Indication> indicationCache) {
        this.indicationCache = indicationCache;
    }

    public ValidationErrorMapper<ValidationError> createCombinedValidationErrorMapper() {
        return new CombinedValidationErrorMapper(this);
    }

    public ValidationErrorMapper<ValidationError.ConditionError> createConditionErrorMapper() {
        return new ConditionErrorMapper(indicationCache);
    }

    public ValidationErrorMapper<ValidationError.ExistingDrugMedicationError> createExistingDrugMedicationErrorMapper() {
        return new ExistingDrugMedicationErrorMapper();
    }

    public ValidationErrorMapper<ValidationError.AndError> createAndErrorMapper(ValidationErrorMapper<ValidationError> combinedValidationErrorMapper) {
        return new AndErrorMapper(combinedValidationErrorMapper);
    }

    public ValidationErrorMapper<ValidationError.OrError> createOrErrorMapper(ValidationErrorMapper<ValidationError> combinedValidationErrorMapper) {
        return new OrErrorMapper(combinedValidationErrorMapper);
    }
}
