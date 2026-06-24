package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedExistingDrugMedicationKeysException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.Operator;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationConditionBuilderTest {
    @InjectMocks
    private ExistingDrugMedicationConditionBuilder existingDrugMedicationConditionBuilder;

    @Test
    void build_WithNonEqualOperator_ThrowsException() {
        var value = new Condition.Value.Structured(Map.of());
        var operator = Operator.GREATER_THAN;

        var e = assertThrows(UnexpectedValueException.class, () -> existingDrugMedicationConditionBuilder.build(operator, value));
        assertEquals(operator.getValue(), e.getValue());
    }

    @Test
    void build_WithSimpleValue_ThrowsException() {
        var value = new Condition.Value.Simple("test");
        var operator = Operator.EQUAL;

        var e = assertThrows(UnexpectedValueException.class, () -> existingDrugMedicationConditionBuilder.build(operator, value));
        assertEquals(value.value(), e.getValue());
    }

    @Test
    void build_WithUnexpectedKey_ThrowsException() {
        var value = new Condition.Value.Structured(
                Map.of(Identifier.FORM_CODE.toString(), "1", Identifier.ROUTE.toString(), "2", "hest", "3"));
        var operator = Operator.EQUAL;

        var e = assertThrows(UnexpectedExistingDrugMedicationKeysException.class, () -> existingDrugMedicationConditionBuilder.build(operator, value));
        assertEquals(Set.of("ATC", "FORM", "ROUTE"), e.getValidKeys());
    }

    @Test
    void build_WithAllExpectedKeys_ReturnsExistingDrugMedicationCondition() {
        var value = new Condition.Value.Structured(
                Map.of(Identifier.ATC_CODE.toString(), "1", Identifier.FORM_CODE.toString(), "2", Identifier.ROUTE.toString(), "3"));
        var operator = Operator.EQUAL;

        var condition = existingDrugMedicationConditionBuilder.build(operator, value);

        var expectedCondition = new ExistingDrugMedicationCondition("1", "2", "3", ExpressionType.EXISTING_DRUG_MEDICATION);
        assertEquals(expectedCondition, condition);
    }

    @Test
    void build_WithEmptyMap_ReturnsExistingDrugMedicationConditionWithWildcards() {
        var value = new Condition.Value.Structured(Map.of());
        var operator = Operator.EQUAL;

        var condition = existingDrugMedicationConditionBuilder.build(operator, value);

        var expectedCondition = new ExistingDrugMedicationCondition("*", "*", "*", ExpressionType.EXISTING_DRUG_MEDICATION);
        assertEquals(expectedCondition, condition);
    }
}