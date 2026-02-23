package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.IndicationCondition;
import org.openapitools.model.Operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class IndicationConditionBuilderTest {
    @InjectMocks
    private IndicationConditionBuilder indicationConditionBuilder;

    @Test
    void build_WithNonEqualOperator_ThrowsException() {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.GREATER_THAN;

        var e = assertThrows(DslParserException.class, () -> indicationConditionBuilder.build(operator, value));
        assertEquals("Unsupported operator for indication condition: >", e.getMessage());
    }

    @Test
    void build_WithEqualOperator_ReturnsIndicationCondition() {
        var value = new Condition.Value.Simple("someSpeciality");
        var operator = Operator.EQUAL;

        var condition = indicationConditionBuilder.build(operator, value);

        var expectedCondition = new IndicationCondition(value.value(), ExpressionType.INDICATION);
        assertEquals(expectedCondition, condition);
    }
}