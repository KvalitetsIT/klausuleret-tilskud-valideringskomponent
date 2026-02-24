package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ClauseInput;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClauseInputDtoModelMapperTest {
    private ClauseInputDtoModelMapper clauseInputDtoModelMapper;
    @Mock
    private Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;

    @BeforeEach
    void setUp() {
        // @InjectMocks can't inject generic Mappers correctly
        clauseInputDtoModelMapper = new ClauseInputDtoModelMapper(expressionDtoModelMapper);
    }

    @Test
    void map_MapsNameAndExpression() {
        var clauseInput = new ClauseInput("Test Clause", Mockito.mock(org.openapitools.model.BinaryExpression.class), "Message");
        var expression = Mockito.mock(BinaryExpression.class);
        Mockito.when(expressionDtoModelMapper.map(clauseInput.getExpression())).thenReturn(expression);

        var mappedClause = clauseInputDtoModelMapper.map(clauseInput);

        assertEquals(clauseInput.getName(), mappedClause.name(), "Clause name should be mapped directly");
        assertEquals(expression, mappedClause.expression(), "Mapped expression should be returned from expression mapper");
        assertEquals(clauseInput.getError(), mappedClause.errorMessage(), "Mapped error message should be mapped directly");
    }
}