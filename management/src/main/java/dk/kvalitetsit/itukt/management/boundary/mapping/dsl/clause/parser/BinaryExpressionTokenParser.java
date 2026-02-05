package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import org.openapitools.model.Expression;

public class BinaryExpressionTokenParser implements TokenParser<Expression> {
    @Override
    public Expression parse(TokenIterator tokens) {
        return null;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return false;
    }
}
