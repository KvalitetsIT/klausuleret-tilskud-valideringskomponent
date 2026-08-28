package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.service.MedicationFormService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;
import java.util.stream.Collectors;

public class ExistingDrugMedicationExpressionValidator implements ExpressionValidator<ExistingDrugMedicationConditionExpression> {
    private final MedicationFormService medicationFormService;

    public ExistingDrugMedicationExpressionValidator(MedicationFormService medicationFormService) {
        this.medicationFormService = medicationFormService;
    }

    @Override
    public List<ExpressionValidationError> validate(ExistingDrugMedicationConditionExpression expression) {
        String formCode = expression.existingDrugMedication().formCode();
        var form = medicationFormService.getForm(formCode);
        return form.isPresent() || ExistingDrugMedicationConditionExpression.WILDCARD.equals(formCode) ? List.of()
                : List.of(
                new UnknownValueError(
                        Identifier.FORM_CODE,
                        formCode,
                        medicationFormService.getForms().stream().map(Medication.Form::code).collect(Collectors.toSet())
                ));
    }
}
