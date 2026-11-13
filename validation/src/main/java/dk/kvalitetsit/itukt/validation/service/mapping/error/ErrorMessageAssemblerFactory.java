package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

public class ErrorMessageAssemblerFactory {
    private final StamdataCacheService<Indication> indicationCache;

    public ErrorMessageAssemblerFactory(StamdataCacheService<Indication> indicationCache) {
        this.indicationCache = indicationCache;
    }

    public ErrorMessageAssembler<ValidationError.ConditionError> createConditionAssembler() {
        return new ConditionErrorMessageAssembler(indicationCache);
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
