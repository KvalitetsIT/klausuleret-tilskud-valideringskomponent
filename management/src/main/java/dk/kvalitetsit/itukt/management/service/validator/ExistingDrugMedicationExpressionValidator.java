package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.service.DrugMedicationFormService;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownFormCodeError;

import java.util.List;

public class ExistingDrugMedicationExpressionValidator implements ExpressionValidator<ExistingDrugMedicationConditionExpression> {
    private final DrugMedicationFormService drugMedicationFormService;

    public ExistingDrugMedicationExpressionValidator(DrugMedicationFormService drugMedicationFormService) {
        this.drugMedicationFormService = drugMedicationFormService;
    }

    @Override
    public List<ExpressionValidationError> validate(ExistingDrugMedicationConditionExpression expression) {
        String formCode = expression.existingDrugMedication().formCode();
        var form = drugMedicationFormService.getForm(formCode);
        return form.isPresent() ? List.of()
                : List.of(new UnknownFormCodeError(formCode, drugMedicationFormService.getForms()));
    }
}
