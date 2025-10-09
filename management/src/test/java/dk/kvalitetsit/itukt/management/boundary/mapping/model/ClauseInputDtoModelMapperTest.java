package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.Error;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClauseInputDtoModelMapperTest {
    private ClauseInputDtoModelMapper clauseInputDtoModelMapper;
    @Mock
    private Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;
    @Mock
    private Mapper<Expression, ExpressionEntity> expressionModelEntityMapper;

    @BeforeEach
    void setUp() {
        // @InjectMocks can't inject generic Mappers correctly
        clauseInputDtoModelMapper = new ClauseInputDtoModelMapper(expressionDtoModelMapper, expressionModelEntityMapper);
    }

    @Test
    void map_MapsNameAndExpression() {
        var clauseInput = new ClauseInput("Test Clause", Mockito.mock(org.openapitools.model.BinaryExpression.class), new Error("Message"));
        var expression = Mockito.mock(BinaryExpression.class);
        var expressionEntity = Mockito.mock(ExpressionEntity.BinaryExpressionEntity.class);
        Mockito.when(expressionDtoModelMapper.map(clauseInput.getExpression())).thenReturn(expression);
        Mockito.when(expressionModelEntityMapper.map(expression)).thenReturn(expressionEntity);

        var mappedClause = clauseInputDtoModelMapper.map(clauseInput);

        assertEquals(clauseInput.getName(), mappedClause.name(), "Clause name should be mapped directly");
        assertEquals(expressionEntity, mappedClause.expression(), "Mapped expression should be mapped from expression mappers");
        assertEquals(clauseInput.getError().getMessage(), mappedClause.errorMessage(), "Mapped error message should be mapped directly");
    }
}