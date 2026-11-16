package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class ErrorMessageAssemblerFactory {

    public ErrorMessageAssembler<ValidationError> createCombinedAssembler() {
        return new CombinedErrorMessageAssembler(this);
    }

    public ErrorMessageAssembler<ValidationError.ConditionError> createConditionAssembler() {
        return new ConditionErrorMessageAssembler();
    }

    public ErrorMessageAssembler<ValidationError.ExistingDrugMedicationError> createExistingDrugMedicationAssembler() {
        return new ExistingDrugMedicationErrorMessageAssembler();
    }

    public ErrorMessageAssembler<ValidationError.AndError> createAndAssembler(ErrorMessageAssembler<ValidationError> combinedAssembler) {
        return new AndErrorMessageAssembler(combinedAssembler);
    }

    public ErrorMessageAssembler<ValidationError.OrError> createOrAssembler(ErrorMessageAssembler<ValidationError> combinedAssembler) {
        return new OrErrorMessageAssembler(combinedAssembler);
    }
}
