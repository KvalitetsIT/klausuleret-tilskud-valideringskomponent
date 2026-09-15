package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationExpressionValidatorTest {
    @Mock
    private StamdataCacheService<Medication.Form> medicationFormService;
    @Mock
    private StamdataCacheService<Medication.ATC> medicationATCService;

    private ExistingDrugMedicationExpressionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ExistingDrugMedicationExpressionValidator(medicationFormService, medicationATCService);
    }

    @Test
    void validate_WhenFormAndAtcIsKnown_ReturnsNoErrors() {
        var form = new Medication.Form("knownFormCode");
        when(medicationFormService.get(form.code())).thenReturn(Optional.of(form));
        var atc = new Medication.ATC("knownAtcCode");
        when(medicationATCService.get(atc.code())).thenReturn(Optional.of(atc));
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(atc.code(), form.code(), ""));

        var result = validator.validate(expression);

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenFormAndAtcAreWildcards_ReturnsNoErrors() {
        when(medicationFormService.get(Mockito.any())).thenReturn(Optional.empty());
        when(medicationATCService.get(Mockito.any())).thenReturn(Optional.empty());
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(
                ExistingDrugMedicationConditionExpression.WILDCARD,
                ExistingDrugMedicationConditionExpression.WILDCARD,
                ""));

        var result = validator.validate(expression);

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenFormIsUnknown_ReturnsUnknownFormCodeError() {
        when(medicationFormService.get(Mockito.any())).thenReturn(Optional.empty());
        String formCode = "ANOTHER_FORM_CODE";
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(
                ExistingDrugMedicationConditionExpression.WILDCARD,
                formCode,
                ExistingDrugMedicationConditionExpression.WILDCARD));

        var result = validator.validate(expression);

        var expected = List.of(new UnknownValueError(Identifier.FORM_CODE, formCode));
        assertEquals(expected, result);
    }

    @Test
    void validate_WhenAtcIsUnknown_ReturnsUnknownAtcCodeError() {
        when(medicationATCService.get(Mockito.any())).thenReturn(Optional.empty());
        String atcCode = "ANOTHER_ATC_CODE";
        var expression = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(
                atcCode,
                ExistingDrugMedicationConditionExpression.WILDCARD,
                ExistingDrugMedicationConditionExpression.WILDCARD));

        var result = validator.validate(expression);

        var expected = List.of(new UnknownValueError(Identifier.ATC_CODE, atcCode));
        assertEquals(expected, result);
    }
}