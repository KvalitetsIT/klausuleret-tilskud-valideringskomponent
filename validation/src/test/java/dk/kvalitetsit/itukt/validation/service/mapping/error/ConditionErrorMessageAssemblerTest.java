package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionErrorMessageAssemblerTest {
    private final ConditionErrorMessageAssembler assembler = new ConditionErrorMessageAssembler();
    @Test
    void assemble_WithEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.EQUAL, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være 0", errorString);
    }

    @Test
    void assemble_WithGreaterThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være større end 0", errorString);
    }

    @Test
    void assemble_WithGreaterOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være større end eller lig 0", errorString);
    }

    @Test
    void assemble_WithLessThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være mindre end 0", errorString);
    }

    @Test
    void assemble_WithLessOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være mindre end eller lig 0", errorString);
    }

    @Test
    void assemble_WithIndicationEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.INDICATION, Operator.EQUAL, "indication");

        String errorString = assembler.assemble(conditionError);

        assertEquals("indikation skal være indication", errorString);
    }
}