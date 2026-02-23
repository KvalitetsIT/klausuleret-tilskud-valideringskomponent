package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder.ConditionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConditionExpressionTokenParserTest {
    @Mock
    private ConditionTokenParser conditionTokenParser;

    @Mock
    private ConditionBuilder ageConditionBuilder;

    @Mock
    private ConditionBuilder indicationConditionBuilder;

    @Mock
    private TokenIterator tokenIterator;

    private ConditionExpressionTokenParser conditionExpressionTokenParser;

    @BeforeEach
    void setUp() {
        Mockito.when(ageConditionBuilder.identifier()).thenReturn(Identifier.AGE);
        Mockito.when(indicationConditionBuilder.identifier()).thenReturn(Identifier.INDICATION);
        conditionExpressionTokenParser = new ConditionExpressionTokenParser(conditionTokenParser, List.of(ageConditionBuilder, indicationConditionBuilder));
    }

    @Test
    void canParse_WhenConditionTokenParserCantParse_ReturnsFalse() {
        Mockito.when(conditionTokenParser.canParse(tokenIterator)).thenReturn(false);

        boolean canParse = conditionExpressionTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenConditionTokenParserCanParse_ReturnsTrue() {
        Mockito.when(conditionTokenParser.canParse(tokenIterator)).thenReturn(true);

        boolean canParse = conditionExpressionTokenParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_WhenNoBuilderMatchesIdentifier_ThrowsException() {
        var condition = Mockito.mock(Condition.SingleValueCondition.class);
        Mockito.when(condition.identifier()).thenReturn(Identifier.DEPARTMENT_SPECIALITY);
        Mockito.when(conditionTokenParser.parse(tokenIterator)).thenReturn(condition);

        var exception = assertThrows(DslParserException.class, () -> conditionExpressionTokenParser.parse(tokenIterator));

        assertEquals("Unsupported identifier: " + Identifier.DEPARTMENT_SPECIALITY, exception.getMessage());
    }

    @Test
    void parse_WithSingleValueCondition_BuildsExpressionWithMatchingConditionBuilder() {
        var singleValueCondition = new Condition.SingleValueCondition(Identifier.AGE, Operator.EQUAL, new Condition.Value.Simple("30"));
        Mockito.when(conditionTokenParser.parse(tokenIterator)).thenReturn(singleValueCondition);
        var ageCondition = Mockito.mock(AgeCondition.class);
        Mockito.when(ageConditionBuilder.build(singleValueCondition.operator(), singleValueCondition.value())).thenReturn(ageCondition);

        var expression = conditionExpressionTokenParser.parse(tokenIterator);

        assertEquals(ageCondition, expression);
    }

    @Test
    void parse_WithEmptyMultiValueCondition_ThrowsException() {
        var multiValueCondition = new Condition.MultiValueCondition(Identifier.INDICATION, List.of());
        Mockito.when(conditionTokenParser.parse(tokenIterator)).thenReturn(multiValueCondition);

        var exception = assertThrows(DslParserException.class, () -> conditionExpressionTokenParser.parse(tokenIterator));

        assertEquals("No values provided for multi-value condition", exception.getMessage());
    }

    @Test
    void parse_WithNonEmptyMultiValueCondition_BuildsOrExpressionUsingMatchingBuilder() {
        var simpleConditionValue1 = new Condition.Value.Simple("A");
        var simpleConditionValue2 = new Condition.Value.Simple("B");
        var simpleConditionValue3 = new Condition.Value.Simple("C");
        var multiValueCondition = new Condition.MultiValueCondition(Identifier.INDICATION, List.of(
                simpleConditionValue1,
                simpleConditionValue2,
                simpleConditionValue3
        ));
        Mockito.when(conditionTokenParser.parse(tokenIterator)).thenReturn(multiValueCondition);
        var indicationConditionA = Mockito.mock(IndicationCondition.class);
        var indicationConditionB = Mockito.mock(IndicationCondition.class);
        var indicationConditionC = Mockito.mock(IndicationCondition.class);
        Mockito.when(indicationConditionBuilder.build(Operator.EQUAL, simpleConditionValue1)).thenReturn(indicationConditionA);
        Mockito.when(indicationConditionBuilder.build(Operator.EQUAL, simpleConditionValue2)).thenReturn(indicationConditionB);
        Mockito.when(indicationConditionBuilder.build(Operator.EQUAL, simpleConditionValue3)).thenReturn(indicationConditionC);

        var expression = conditionExpressionTokenParser.parse(tokenIterator);

        var orExpression1 = assertInstanceOf(BinaryExpression.class, expression);
        assertEquals(BinaryOperator.OR, orExpression1.getOperator());
        var orExpression2 = assertInstanceOf(BinaryExpression.class, orExpression1.getLeft());
        assertEquals(BinaryOperator.OR, orExpression2.getOperator());
        assertEquals(indicationConditionA, orExpression2.getLeft());
        assertEquals(indicationConditionB, orExpression2.getRight());
        assertEquals(indicationConditionC, orExpression1.getRight());
    }
}