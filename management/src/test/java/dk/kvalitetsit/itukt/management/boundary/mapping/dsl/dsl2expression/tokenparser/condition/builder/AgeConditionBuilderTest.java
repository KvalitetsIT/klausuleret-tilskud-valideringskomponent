package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.Operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AgeConditionBuilderTest {
    @InjectMocks
    private AgeConditionBuilder ageConditionBuilder;

    @Test
    void build_WithNonIntegerValue_ThrowsException() {
        var value = new Condition.Value.Simple("notAnInteger");

        var e = assertThrows(DslParserException.class, () -> ageConditionBuilder.build(Operator.EQUAL, value));
        assertEquals("Invalid value for age condition: notAnInteger", e.getMessage());
    }

    @Test
    void build_WithIntegerValue_ReturnsAgeCondition() {
        var value = new Condition.Value.Simple("5");

        var ageCondition = ageConditionBuilder.build(Operator.EQUAL, value);

        var expectedCondition = new AgeCondition(Operator.EQUAL, 5, ExpressionType.AGE);
        assertEquals(expectedCondition, ageCondition);
    }
}