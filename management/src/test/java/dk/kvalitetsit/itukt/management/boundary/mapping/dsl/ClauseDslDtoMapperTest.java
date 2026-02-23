package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.DslInput;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClauseDslDtoMapperTest {
    @Mock
    private DslParser dslParser;

    @InjectMocks
    private ClauseDslDtoMapper clauseDslDtoMapper;

    @Test
    void map_ParsesDslAndMapsInput() {
        var dslInput = new DslInput("name", "dsl", "error");
        var expression = Mockito.mock(AgeCondition.class);
        Mockito.when(dslParser.parse(dslInput.getDsl())).thenReturn(expression);

        var result = clauseDslDtoMapper.map(dslInput);

        assertEquals(dslInput.getName(), result.getName());
        assertEquals(expression, result.getExpression());
        assertEquals(dslInput.getError(), result.getError());
    }
}