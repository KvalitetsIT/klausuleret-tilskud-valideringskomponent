package dk.kvalitetsit.itukt.validation.service.model;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistingDrugMedicationConditionExpressionTest {

    @Test
    void matches_WhenAllFieldsMatchesOneOutOfMultipleItems_ReturnsTrue() {
        var existingDrugMedication1 = new ExistingDrugMedication("atc", "form", "adm");
        var existingDrugMedication2 = new ExistingDrugMedication("no-match", "no-match", "no-match");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication1, existingDrugMedication2, "another item"));

        assertTrue(matches);
    }

    @Test
    void matches_WhenAtcCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("wrong-atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertFalse(matches);
    }

    @Test
    void matches_WhenFormCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "wrong-form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertFalse(matches);
    }

    @Test
    void matches_WhenRouteOfAdministrationCodeDoesNotMatchCondition_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "wrong-adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertFalse(matches);
    }

    @Test
    void matches_WithAtcCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("*", "form", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertTrue(matches);
    }

    @Test
    void matches_WithFormCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "*", "adm");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertTrue(matches);
    }

    @Test
    void matches_WithRouteOfAdministrationCodeWildcardAndOtherFieldsMatch_ReturnsTrue() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "*");

        boolean matches = condition.matches(List.of(existingDrugMedication));

        assertTrue(matches);
    }

    @Test
    void matches_WithWildcardAndOtherFieldsDoesNotMatch_ReturnsFalse() {
        var existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var condition = new ExistingDrugMedicationConditionExpression("*", "wrong-form", "adm");

        boolean matches = condition.matches(existingDrugMedication);

        assertFalse(matches);
    }

    @Test
    void matches_WhenNoItemsInList_ReturnsFalse() {
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches(List.of());

        assertFalse(matches);
    }

    @Test
    void matches_WhenValueIsNotAList_ReturnsFalse() {
        var condition = new ExistingDrugMedicationConditionExpression("atc", "form", "adm");

        boolean matches = condition.matches("not-a-list");

        assertFalse(matches);
    }
}