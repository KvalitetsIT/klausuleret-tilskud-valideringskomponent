package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.service.MedicationFormService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationExpressionValidatorTest {
    @Mock
    private MedicationFormService medicationFormService;

    @InjectMocks
    private ExistingDrugMedicationExpressionValidator validator;

    @Test
    void validate_WhenFormIsKnown_ReturnsNoErrors() {
        var form = new Medication.Form("knownFormCode");
        when(medicationFormService.getForm(form.code())).thenReturn(Optional.of(form));
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("", form.code(), ""));

        var result = validator.validate(expression);

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenFormIsWildcard_ReturnsNoErrors() {
        when(medicationFormService.getForm(Mockito.any())).thenReturn(Optional.empty());
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("", ExistingDrugMedicationConditionExpression.WILDCARD, ""));

        var result = validator.validate(expression);

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenFormIsUnknown_ReturnsUnknownFormCodeError() {
        String knownFormCode = "KNOWN_FORM_CODE";
        when(medicationFormService.getForm(Mockito.any())).thenReturn(Optional.empty());
        when(medicationFormService.getForms()).thenReturn(Set.of(new Medication.Form(knownFormCode)));
        String formCode = "ANOTHER_FORM_CODE";
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("", formCode, ""));

        var result = validator.validate(expression);

        var expected = List.of(new UnknownValueError(Identifier.FORM_CODE, formCode, Set.of(knownFormCode)));
        assertEquals(expected, result);
    }
}