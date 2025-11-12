package dk.kvalitetsit.itukt.common.model;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExistingDrugMedicationConditionExpressionTest {

    @Test
    void matches_WhenAllFieldsMatchesOneOutOfMultipleItems_ReturnsTrue() {
        var existingDrugMedication1 = new ExistingDrugMedication("atc", "form", "adm");
        var existingDrugMedication2 = new ExistingDrugMedication("no-match", "no-match", "no-match");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication1, existingDrugMedication2)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when one of multiple items matches");
    }

    @Test
    void matches_WhenAtcCodeDoesNotMatchCondition_ReturnsFalse() {
        String atc = "atc", form = "form", adm = "adm";
        var existingDrugMedication = new ExistingDrugMedication("wrong-atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(atc, form, adm);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(atc, error.atcCode());
        assertEquals(form, error.formCode());
        assertEquals(adm, error.routeOfAdministrationCode());
    }

    @Test
    void matches_WhenFormCodeDoesNotMatchCondition_ReturnsFalse() {
        String atc = "atc", form = "form", adm = "adm";
        var existingDrugMedication = new ExistingDrugMedication("atc", "wrong=form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(atc, form, adm);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(atc, error.atcCode());
        assertEquals(form, error.formCode());
        assertEquals(adm, error.routeOfAdministrationCode());
    }

    @Test
    void matches_WhenRouteOfAdministrationCodeDoesNotMatchCondition_ReturnsFalse() {
        String atc = "atc", form = "form", adm = "adm";
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "wrong-adm");
        var condition = new ExistingDrugMedicationConditionExpression(atc, form, adm);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(atc, error.atcCode());
        assertEquals(form, error.formCode());
        assertEquals(adm, error.routeOfAdministrationCode());
    }

    @Test
    void matches_WithAtcCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("*", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when ATC code is wildcard and other fields match");
    }

    @Test
    void matches_WithFormCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "*", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when form code is wildcard and other fields match");
    }

    @Test
    void matches_WithRouteOfAdministrationCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "*");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when route of administration code is wildcard and other fields match");
    }

    @Test
    void matches_WithWildcardAndOtherFieldsDoesNotMatch_ReturnsFalse() {
        String atc = "*", form = "wrong-form", adm = "adm";
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(atc, form, adm);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(atc, error.atcCode());
        assertEquals(form, error.formCode());
        assertEquals(adm, error.routeOfAdministrationCode());
    }

    @Test
    void matches_WhenNoItemsInList_ReturnsFalse() {
        String atc = "atc", form = "form", adm = "adm";
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of()));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(atc, error.atcCode());
        assertEquals(form, error.formCode());
        assertEquals(adm, error.routeOfAdministrationCode());
    }

    @Test
    void matches_WhenNoExistingDrugMedicationInValidationInput_ThrowsExistingDrugMedicationRequiredException() {
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.empty());

        assertEquals(Optional.of(new ValidationFailed.ExistingDrugMedicationRequired()), condition.validates(validationInput),
                "Expected ExistingDrugMedicationRequired when no existing drug medication is present in validation input");
    }
}