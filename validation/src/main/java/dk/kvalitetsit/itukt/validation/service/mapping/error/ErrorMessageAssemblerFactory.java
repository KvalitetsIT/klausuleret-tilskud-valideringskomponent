package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

public class ErrorMessageAssemblerFactory {
    private final StamdataCacheService<Indication> indicationCache;
    private final StamdataCacheService<Medication.ATC> atcCache;
    private final StamdataCacheService<Medication.Form> formCache;

    public ErrorMessageAssemblerFactory(
            StamdataCacheService<Indication> indicationCache,
            StamdataCacheService<Medication.ATC> atcCache,
            StamdataCacheService<Medication.Form> formCache) {
        this.indicationCache = indicationCache;
        this.atcCache = atcCache;
        this.formCache = formCache;
    }

    public ErrorMessageAssembler<ValidationError.ConditionError> createConditionAssembler() {
        return new ConditionErrorMessageAssembler(indicationCache);
    }

    public ErrorMessageAssembler<ValidationError.ExistingDrugMedicationError> createExistingDrugMedicationAssembler() {
        return new ExistingDrugMedicationErrorMessageAssembler(atcCache, formCache);
    }

    public ErrorMessageAssembler<ValidationError.AndError> createAndAssembler(ErrorMessageAssembler<ValidationError> combinedAssembler) {
        return new AndErrorMessageAssembler(combinedAssembler);
    }

    public ErrorMessageAssembler<ValidationError.OrError> createOrAssembler(ErrorMessageAssembler<ValidationError> combinedAssembler) {
        return new OrErrorMessageAssembler(combinedAssembler);
    }
}
