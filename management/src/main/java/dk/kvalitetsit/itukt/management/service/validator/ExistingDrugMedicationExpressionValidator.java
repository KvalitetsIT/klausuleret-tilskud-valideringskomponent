package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.service.MedicationATCService;
import dk.kvalitetsit.itukt.common.service.MedicationFormService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExistingDrugMedicationExpressionValidator implements ExpressionValidator<ExistingDrugMedicationConditionExpression> {
    private final MedicationFormService medicationFormService;
    private final MedicationATCService medicationATCService;

    public ExistingDrugMedicationExpressionValidator(MedicationFormService medicationFormService, MedicationATCService medicationATCService) {
        this.medicationFormService = medicationFormService;
        this.medicationATCService = medicationATCService;
    }

    @Override
    public List<ExpressionValidationError> validate(ExistingDrugMedicationConditionExpression expression) {
        String formCode = expression.existingDrugMedication().formCode();

        return Stream.of(
                        validateFormCode(formCode),
                        validateAtcCode(expression.existingDrugMedication().atcCode()))
                .flatMap(Optional::stream).toList();
    }

    private Optional<ExpressionValidationError> validateFormCode(String formCode) {
        var form = medicationFormService.getForm(formCode);
        return form.isPresent() || ExistingDrugMedicationConditionExpression.WILDCARD.equals(formCode) ? Optional.empty()
                : Optional.of(
                new UnknownValueError(
                        Identifier.FORM_CODE,
                        formCode,
                        medicationFormService.getForms().stream().map(Medication.Form::code).collect(Collectors.toSet())
                ));
    }

    private Optional<ExpressionValidationError> validateAtcCode(String atcCode) {
        var atc = medicationATCService.getATC(atcCode);
        return atc.isPresent() || ExistingDrugMedicationConditionExpression.WILDCARD.equals(atcCode) ? Optional.empty()
                : Optional.of(
                new UnknownValueError(
                        Identifier.ATC_CODE,
                        atcCode,
                        medicationATCService.getATCs().stream().map(Medication.ATC::code).collect(Collectors.toSet())
                ));
    }
}
