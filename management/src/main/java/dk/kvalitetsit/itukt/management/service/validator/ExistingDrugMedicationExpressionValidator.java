package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ExistingDrugMedicationExpressionValidator implements ExpressionValidator<ExistingDrugMedicationConditionExpression> {
    private final StamdataCacheService<Medication.Form> medicationFormService;
    private final StamdataCacheService<Medication.ATC> medicationATCService;

    public ExistingDrugMedicationExpressionValidator(StamdataCacheService<Medication.Form> medicationFormService, StamdataCacheService<Medication.ATC> medicationATCService) {
        this.medicationFormService = medicationFormService;
        this.medicationATCService = medicationATCService;
    }

    @Override
    public List<ExpressionValidationError> validate(ExistingDrugMedicationConditionExpression expression) {
        return Stream.of(
                        validate(Identifier.FORM_CODE, medicationFormService, expression.existingDrugMedication().formCode()),
                        validate(Identifier.ATC_CODE, medicationATCService, expression.existingDrugMedication().atcCode()))
                .flatMap(Optional::stream).toList();
    }

    private Optional<ExpressionValidationError> validate(Identifier identifier, StamdataCacheService<?> cacheService, String value) {
        var valueIsKnown = cacheService.get(value).isPresent();
        return valueIsKnown || ExistingDrugMedicationConditionExpression.WILDCARD.equals(value) ? Optional.empty()
                : Optional.of(
                new UnknownValueError(
                        identifier,
                        value
                ));
    }
}
