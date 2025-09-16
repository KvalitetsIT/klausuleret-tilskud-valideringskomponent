package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistingDrugMedicationConditionExpressionTest {

    @Test
    void matches_WhenAllFieldsMatchesOneOutOfMultipleItems_ReturnsTrue() {
        var existingDrugMedication1 = new ExistingDrugMedication("atc", "form", "adm");
        var existingDrugMedication2 = new ExistingDrugMedication("no-match", "no-match", "no-match");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication1, existingDrugMedication2));

        boolean validates = condition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WhenAtcCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("wrong-atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenFormCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "wrong-form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenRouteOfAdministrationCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "wrong-adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithAtcCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("*", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithFormCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "*", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithRouteOfAdministrationCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "*");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithWildcardAndOtherFieldsDoesNotMatch_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("*", "wrong-form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of(existingDrugMedication));

        boolean validates = condition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenNoItemsInList_ReturnsFalse() {
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.existingDrugMedication()).thenReturn(List.of());

        boolean validates = condition.validates(validationInput);

        assertFalse(validates);
    }
}