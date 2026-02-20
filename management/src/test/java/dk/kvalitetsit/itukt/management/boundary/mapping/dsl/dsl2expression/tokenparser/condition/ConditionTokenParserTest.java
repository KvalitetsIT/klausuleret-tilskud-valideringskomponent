package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Operator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConditionTokenParserTest {
    @Mock
    private TokenIterator tokenIterator;

    @Mock
    private StructuredValueTokenParser structuredValueTokenParser;

    @Mock
    private MultiValueTokenParser multiValueTokenParser;

    @InjectMocks
    private ConditionTokenParser conditionTokenParser;

    @Test
    void canParse_WhenNextTokenIsNotAValue_ReturnsFalse() {
        Mockito.when(tokenIterator.nextHasType(TokenType.VALUE)).thenReturn(false);

        boolean canParse = conditionTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenNextTokenDoesNotHaveIdentifierText_ReturnsFalse() {
        Mockito.when(tokenIterator.nextHasType(TokenType.VALUE)).thenReturn(true);
        Mockito.when(tokenIterator.nextHasText(Mockito.anyString())).thenReturn(false);

        boolean canParse = conditionTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @ParameterizedTest
    @EnumSource(Identifier.class)
    void canParse_WhenNextTokenValueHasIdentifier_ReturnsTrue(Identifier identifier) {
        Mockito.when(tokenIterator.nextHasType(TokenType.VALUE)).thenReturn(true);
        Mockito.lenient().when(tokenIterator.nextHasText(Mockito.argThat(text -> !text.equals(identifier.toString()))))
                .thenReturn(false);
        Mockito.when(tokenIterator.nextHasText(identifier.toString())).thenReturn(true);

        boolean canParse = conditionTokenParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_WithMultiValueTokenButUnexpectedOperator_ThrowsException() {
        Mockito.when(multiValueTokenParser.canParse(tokenIterator)).thenReturn(true);
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, Identifier.INDICATION.toString()));
        Mockito.when(tokenIterator.nextWithType(TokenType.OPERATOR))
                .thenReturn(new Token(TokenType.OPERATOR, "unexpectedOperator"));

        DslParserException e = assertThrows(DslParserException.class, () -> conditionTokenParser.parse(tokenIterator));

        assertEquals("Unexpected operator for multi-value condition: unexpectedOperator", e.getMessage());
    }

    @Test
    void parse_WithMultiValueTokenAndExpectedOperator_ReturnsParsedMultiValueCondition() {
        Mockito.when(multiValueTokenParser.canParse(tokenIterator)).thenReturn(true);
        List<Condition.Value> values = List.of(Mockito.mock(Condition.Value.Simple.class));
        Mockito.when(multiValueTokenParser.parse(tokenIterator))
                .thenReturn(values);
        Identifier identifier = Identifier.INDICATION;
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, identifier.toString()));
        Mockito.when(tokenIterator.nextWithType(TokenType.OPERATOR))
                .thenReturn(new Token(TokenType.OPERATOR, "i"));

        Condition condition = conditionTokenParser.parse(tokenIterator);

        var expectedCondition = new Condition.MultiValueCondition(identifier, values);
        assertEquals(expectedCondition, condition);
    }

    @Test
    void parse_WithSimpleValueToken_ReturnsParsedSimpleValueCondition() {
        Mockito.when(multiValueTokenParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(structuredValueTokenParser.canParse(tokenIterator)).thenReturn(false);
        Identifier identifier = Identifier.AGE;
        Operator operator = Operator.GREATER_THAN;
        String value = "test";
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, identifier.toString()))
                .thenReturn(new Token(TokenType.VALUE, value));
        Mockito.when(tokenIterator.nextWithType(TokenType.OPERATOR))
                .thenReturn(new Token(TokenType.OPERATOR, operator.getValue()));

        Condition condition = conditionTokenParser.parse(tokenIterator);

        var expectedCondition = new Condition.SingleValueCondition(identifier, operator, new Condition.Value.Simple(value));
        assertEquals(expectedCondition, condition);
    }

    @Test
    void parse_WithStructuredValueToken_ReturnsParsedStructuredValueCondition() {
        Mockito.when(multiValueTokenParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(structuredValueTokenParser.canParse(tokenIterator)).thenReturn(true);
        Identifier identifier = Identifier.AGE;
        Operator operator = Operator.GREATER_THAN;
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, identifier.toString()));
        Mockito.when(tokenIterator.nextWithType(TokenType.OPERATOR))
                .thenReturn(new Token(TokenType.OPERATOR, operator.getValue()));
        var structuredValue = Mockito.mock(Condition.Value.Structured.class);
        Mockito.when(structuredValueTokenParser.parse(tokenIterator)).thenReturn(structuredValue);

        Condition condition = conditionTokenParser.parse(tokenIterator);

        var expectedCondition = new Condition.SingleValueCondition(identifier, operator, structuredValue);
        assertEquals(expectedCondition, condition);
    }
}