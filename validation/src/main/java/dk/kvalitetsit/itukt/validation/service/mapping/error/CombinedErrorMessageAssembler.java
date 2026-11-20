package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class CombinedErrorMessageAssembler implements ErrorMessageAssembler<ValidationError> {
    private final ErrorMessageAssembler<ValidationError.ConditionError> conditionAssembler;
    private final ErrorMessageAssembler<ValidationError.ExistingDrugMedicationError> existingDrugMedicationAssembler;
    private final ErrorMessageAssembler<ValidationError.AndError> andAssembler;
    private final ErrorMessageAssembler<ValidationError.OrError> orAssembler;

    public CombinedErrorMessageAssembler(ErrorMessageAssemblerFactory factory) {
        this.conditionAssembler = factory.createConditionAssembler();
        this.existingDrugMedicationAssembler = factory.createExistingDrugMedicationAssembler();
        this.andAssembler = factory.createAndAssembler(this);
        this.orAssembler = factory.createOrAssembler(this);
    }

    @Override
    public String assemble(ValidationError error) {
        return switch (error) {
            case ValidationError.ConditionError conditionError -> conditionAssembler.assemble(conditionError);
            case ValidationError.ExistingDrugMedicationError existingDrugMedicationError ->
                    existingDrugMedicationAssembler.assemble(existingDrugMedicationError);
            case ValidationError.AndError andError -> andAssembler.assemble(andError);
            case ValidationError.OrError orError -> orAssembler.assemble(orError);
        };
    }
}
