package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ConditionErrorMessageAssemblerTest {
    @Mock
    private StamdataCacheService<Indication> indicationCache;

    @InjectMocks
    private ConditionErrorMessageAssembler assembler;

    @Test
    void assemble_WithEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.EQUAL, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være '0'", errorString);
    }

    @Test
    void assemble_WithGreaterThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være større end '0'", errorString);
    }

    @Test
    void assemble_WithGreaterOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være større end eller lig '0'", errorString);
    }

    @Test
    void assemble_WithLessThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være mindre end '0'", errorString);
    }

    @Test
    void assemble_WithLessOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, "0");

        String errorString = assembler.assemble(conditionError);

        assertEquals("alder skal være mindre end eller lig '0'", errorString);
    }

    @Test
    void assemble_WithIndicationEqualsOperator_ReturnsErrorStringWithMappedValue() {
        String indicationCode = "1234";
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.INDICATION, Operator.EQUAL, indicationCode);
        Indication indication = new Indication(1234L, "mapped indication");
        Mockito.when(indicationCache.get(indicationCode)).thenReturn(Optional.of(indication));

        String errorString = assembler.assemble(conditionError);

        assertEquals("indikation skal være '%s' (%s)".formatted(indication.text(), indicationCode), errorString);
    }
}