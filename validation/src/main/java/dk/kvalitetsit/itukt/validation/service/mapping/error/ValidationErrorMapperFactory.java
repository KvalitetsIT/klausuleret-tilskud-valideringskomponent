package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class ValidationErrorMapperFactory {

    public ValidationErrorMapper<ValidationError> createCombinedValidationErrorMapper() {
        return new CombinedValidationErrorMapper(this);
    }

    public ValidationErrorMapper<ValidationError.ConditionError> createConditionErrorMapper() {
        return new ConditionErrorMapper();
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
