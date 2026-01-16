package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorTest {

    @Test
    void conditionErrorToErrorString_WithEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.EQUAL, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være 0", errorString);
    }

    @Test
    void conditionErrorToErrorString_WithGreaterThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være større end 0", errorString);
    }

    @Test
    void conditionErrorToErrorString_WithGreaterOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være større end eller lig 0", errorString);
    }

    @Test
    void conditionErrorToErrorString_WithLessThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være mindre end 0", errorString);
    }

    @Test
    void conditionErrorToErrorString_WithLessOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være mindre end eller lig 0", errorString);
    }

    @Test
    void conditionErrorToErrorString_WithIndicationEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.INDICATION, Operator.EQUAL, "indication");

        String errorString = conditionError.toErrorString();

        assertEquals("indikation skal være indication", errorString);
    }

    @Test
    void andErrorToErrorString_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(leftError.toErrorString()).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(rightError.toErrorString()).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andError.toErrorString();

        assertEquals("left error og right error", errorString);
    }

    @Test
    void orErrorToErrorString_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(leftError.toErrorString()).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(rightError.toErrorString()).thenReturn("right error");
        var orError = new ValidationError.OrError(leftError, rightError);

        String errorString = orError.toErrorString();

        assertEquals("left error eller right error", errorString);
    }

    @Test
    void existingDrugMedicationErrorToErrorString_ReturnsCorrespondingErrorString() {
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = conditionError.toErrorString();

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: ATC = atc, Formkode = form, Administrationsrutekode = adm", errorString);
    }
}