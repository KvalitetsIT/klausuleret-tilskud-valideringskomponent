package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExistingDrugMedicationConditionExpressionTest {

    @Test
    void validates_WhenAllFieldsMatchesButDifferentCase_ReturnsNoErrors() {
        var requiredDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(requiredDrugMedication);
        var inputDrugMedication = new ExistingDrugMedication("aTc", "fOrM", "AdM");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(inputDrugMedication)));

        var validationErrors = condition.validates(validationInput);

        assertTrue(validationErrors.isEmpty(), "Expected condition to validate when all fields match regardless of case");
    }

    @Test
    void validates_WhenAllFieldsMatchesOneOutOfMultipleItems_ReturnsNoErrors() {
        var existingDrugMedication1 = new ExistingDrugMedication("atc", "form", "adm");
        var existingDrugMedication2 = new ExistingDrugMedication("no-match", "no-match", "no-match");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication1);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication1, existingDrugMedication2)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when one of multiple items matches");
    }

    @Test
    void validates_WhenAtcCodeDoesNotMatchCondition_ReturnsValidationError() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(new ExistingDrugMedication("wrong-atc", "form", "adm"))));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(existingDrugMedication, error.existingDrugMedication());
    }

    @Test
    void validates_WhenFormCodeDoesNotMatchCondition_ReturnsValidationError() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(new ExistingDrugMedication("atc", "wrong=form", "adm"))));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(existingDrugMedication, error.existingDrugMedication());
    }

    @Test
    void validates_WhenRouteOfAdministrationCodeDoesNotMatchCondition_ReturnsValidationError() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(new ExistingDrugMedication("atc", "form", "wrong-adm"))));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(existingDrugMedication, error.existingDrugMedication());
    }

    @Test
    void validates_WithAtcCodeWildcardAndOtherFieldsMatch_ReturnsNoErrors() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("*", "form", "adm"));
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when ATC code is wildcard and other fields match");
    }

    @Test
    void validates_WithFormCodeWildcardAndOtherFieldsMatch_ReturnsNoErrors() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc", "*", "adm"));
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when form code is wildcard and other fields match");
    }

    @Test
    void validates_WithRouteOfAdministrationCodeWildcardAndOtherFieldsMatch_ReturnsNoErrors() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc", "form", "*"));
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(existingDrugMedication)));

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates, "Expected condition to validate when route of administration code is wildcard and other fields match");
    }

    @Test
    void validates_WithWildcardAndOtherFieldsDoesNotMatch_ReturnsValidationError() {
        var existingDrugMedication = new ExistingDrugMedication("*", "wrong-form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of(new ExistingDrugMedication("atc", "form", "adm"))));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(existingDrugMedication, error.existingDrugMedication());
    }

    @Test
    void validates_WhenNoItemsInList_ReturnsValidationError() {

        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.of(List.of()));

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ValidationError.ExistingDrugMedicationError.class, validationError.get());
        assertEquals(existingDrugMedication, error.existingDrugMedication());
    }

    @Test
    void validates_WhenNoExistingDrugMedicationInValidationInput_ThrowsExistingDrugMedicationRequiredException() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression(existingDrugMedication);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(Optional.empty());

        assertEquals(Optional.of(new ValidationFailed.ExistingDrugMedicationRequired()), condition.validates(validationInput),
                "Expected ExistingDrugMedicationRequired when no existing drug medication is present in validation input");
    }
}