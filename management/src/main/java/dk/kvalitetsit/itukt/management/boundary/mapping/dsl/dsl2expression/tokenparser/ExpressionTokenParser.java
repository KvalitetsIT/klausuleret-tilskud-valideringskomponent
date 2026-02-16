package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import org.openapitools.model.Expression;

/**
 * The overall token parser.
 * Simply forwards to BinaryExpressionTokenParser which handles any expression
 */
public class ExpressionTokenParser implements TokenParser<Expression> {
    private final BinaryExpressionTokenParser binaryExpressionTokenParser;

    public ExpressionTokenParser(TokenParserFactory tokenParserFactory) {
        this.binaryExpressionTokenParser = tokenParserFactory.createBinaryExpressionTokenParser(this);
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return binaryExpressionTokenParser.canParse(tokens);
    }

    @Override
    public Expression parse(TokenIterator tokens) {
        return binaryExpressionTokenParser.parse(tokens);
    }
}
