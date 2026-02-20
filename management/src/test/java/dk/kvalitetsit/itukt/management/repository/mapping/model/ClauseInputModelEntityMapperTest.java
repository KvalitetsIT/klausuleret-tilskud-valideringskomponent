package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClauseInputModelEntityMapperTest {
    @Mock
    private ExpressionModelEntityMapper expressionMapper;

    @InjectMocks
    private ClauseInputModelEntityMapper clauseInputMapper;

    @Test
    void map_MapsAllFields() {
        var expression = Mockito.mock(AgeConditionExpression.class);
        var clauseInput = new ClauseFullInput("test name", expression, "test error", Clause.Status.DRAFT, new Date());
        var expectedExpressionEntity = Mockito.mock(ExpressionEntity.StringConditionEntity.class);
        Mockito.when(expressionMapper.map(expression)).thenReturn(expectedExpressionEntity);

        var result = clauseInputMapper.map(clauseInput);

        assertEquals(clauseInput.name(), result.name(), "Name should be mapped correctly");
        assertEquals(expectedExpressionEntity, result.expression(), "Expression should be mapped using the expression mapper");
        assertEquals(clauseInput.errorMessage(), result.errorMessage(), "Error message should be mapped correctly");
        assertEquals(clauseInput.status(), result.status(), "Status should be mapped correctly");
        assertEquals(clauseInput.validFrom(), result.validFrom(), "Valid from date should be mapped correctly");
    }
}