package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import org.openapitools.model.Expression;

public class ExpressionTokenParser implements TokenParser<Expression> {
    private final BinaryExpressionTokenParser binaryExpressionTokenParser;

    public ExpressionTokenParser(TokenParserFactory tokenParserFactory) {
        this.binaryExpressionTokenParser = tokenParserFactory.createBinaryExpressionTokenParser(this);
    }

    @Override
    public Expression parse(TokenIterator tokens) {
        return binaryExpressionTokenParser.parse(tokens);
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return binaryExpressionTokenParser.canParse(tokens);
    }
}
