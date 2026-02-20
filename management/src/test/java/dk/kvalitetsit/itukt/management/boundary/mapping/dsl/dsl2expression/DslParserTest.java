package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.ExpressionTokenParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DslParserTest {
    @Mock
    private ExpressionTokenParser expressionTokenParser;

    @Mock
    private Lexer lexer;

    @InjectMocks
    private DslParser dslParser;

    @Test
    void parse_GetsAndParsesTokens() {
        String dsl = "some dsl";
        var tokenIterator = Mockito.mock(TokenIterator.class);
        Mockito.when(lexer.getTokens(dsl)).thenReturn(tokenIterator);
        var expectedExpression = Mockito.mock(AgeCondition.class);
        Mockito.when(expressionTokenParser.parse(tokenIterator)).thenReturn(expectedExpression);

        var parsedExpression = dslParser.parse(dsl);

        assertEquals(expectedExpression, parsedExpression);
        Mockito.verify(tokenIterator).expectNoMoreTokens();
    }
}