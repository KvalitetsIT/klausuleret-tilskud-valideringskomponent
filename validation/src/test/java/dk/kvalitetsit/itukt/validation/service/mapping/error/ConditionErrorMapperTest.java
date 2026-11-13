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
class ConditionErrorMapperTest {
    @Mock
    private StamdataCacheService<Indication> indicationCache;

    @InjectMocks
    private ConditionErrorMapper conditionErrorMapper;

    @Test
    void map_WithEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.EQUAL, "0");

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("alder skal være 0", errorString);
    }

    @Test
    void map_WithGreaterThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN, "0");

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("alder skal være større end 0", errorString);
    }

    @Test
    void map_WithGreaterOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, "0");

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("alder skal være større end eller lig 0", errorString);
    }

    @Test
    void map_WithLessThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN, "0");

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("alder skal være mindre end 0", errorString);
    }

    @Test
    void map_WithLessOrEqualsThanOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, "0");

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("alder skal være mindre end eller lig 0", errorString);
    }

    @Test
    void map_WithIndicationEqualsOperator_ReturnsErrorStringWithMappedValue() {
        String indicationCode = "1234";
        var conditionError = new ValidationError.ConditionError(ValidationError.ConditionError.Field.INDICATION, Operator.EQUAL, indicationCode);
        Indication indication = new Indication(1L, "mapped indication");
        Mockito.when(indicationCache.get(indicationCode)).thenReturn(Optional.of(indication));

        String errorString = conditionErrorMapper.map(conditionError);

        assertEquals("indikation skal være " + indication.text(), errorString);
    }
}