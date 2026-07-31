package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.DslUpdateInput;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClauseDslUpdateModelMapperTest {
    @Mock
    private DslParser dslParser;

    @Mock
    private Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;

    @InjectMocks
    private ClauseDslUpdateModelMapper clauseDslUpdateModelMapper;

    @Test
    void map() {
        var dslUpdateInput = new DslUpdateInput("test-dsl", "test-error");
        var dtoExpression = Mockito.mock(org.openapitools.model.AgeCondition.class);
        Mockito.when(dslParser.parse(dslUpdateInput.getDsl())).thenReturn(dtoExpression);
        var modelExpression = Mockito.mock(AgeConditionExpression.class);
        Mockito.when(expressionDtoModelMapper.map(dtoExpression)).thenReturn(modelExpression);

        var updateInput = clauseDslUpdateModelMapper.map(dslUpdateInput);
        var expected = new ClauseUpdateInput(modelExpression, dslUpdateInput.getError());
        assertEquals(expected, updateInput);
    }
}