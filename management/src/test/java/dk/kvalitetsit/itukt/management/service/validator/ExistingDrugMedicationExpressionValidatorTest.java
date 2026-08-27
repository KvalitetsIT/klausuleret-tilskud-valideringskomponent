package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.service.DrugMedicationFormService;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownFormCodeError;
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
    private DrugMedicationFormService drugMedicationFormService;

    @InjectMocks
    private ExistingDrugMedicationExpressionValidator validator;

    @Test
    void validate_WhenFormIsKnown_ReturnsNoErrors() {
        var form = new DrugMedication.Form("knownFormCode");
        when(drugMedicationFormService.getForm(form.code())).thenReturn(Optional.of(form));
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("", form.code(), ""));

        var result = validator.validate(expression);

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenFormIsUnknown_ReturnsUnknownFormCodeError() {
        var knownForms = Set.of(new DrugMedication.Form("knownFormCode"));
        when(drugMedicationFormService.getForm(Mockito.any())).thenReturn(Optional.empty());
        when(drugMedicationFormService.getForms()).thenReturn(knownForms);
        String formCode = "anotherFormCode";
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("", formCode, ""));

        var result = validator.validate(expression);

        var expected = List.of(new UnknownFormCodeError(formCode, knownForms));
        assertEquals(expected, result);
    }
}