package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class CombinedValidationErrorMapper implements ValidationErrorMapper<ValidationError> {
    private final ValidationErrorMapper<ValidationError.ConditionError> conditionErrorMapper;
    private final ValidationErrorMapper<ValidationError.ExistingDrugMedicationError> existingDrugMedicationErrorMapper;
    private final ValidationErrorMapper<ValidationError.AndError> andErrorMapper;
    private final ValidationErrorMapper<ValidationError.OrError> orErrorMapper;

    public CombinedValidationErrorMapper(ValidationErrorMapperFactory factory) {
        this.conditionErrorMapper = factory.createConditionErrorMapper();
        this.existingDrugMedicationErrorMapper = factory.createExistingDrugMedicationErrorMapper();
        this.andErrorMapper = factory.createAndErrorMapper(this);
        this.orErrorMapper = factory.createOrErrorMapper(this);
    }

    @Override
    public String map(ValidationError error) {
        return switch (error) {
            case ValidationError.ConditionError conditionError -> conditionErrorMapper.map(conditionError);
            case ValidationError.ExistingDrugMedicationError existingDrugMedicationError ->
                    existingDrugMedicationErrorMapper.map(existingDrugMedicationError);
            case ValidationError.AndError andError -> andErrorMapper.map(andError);
            case ValidationError.OrError orError -> orErrorMapper.map(orError);
        };
    }
}
