package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

public final class UnexpectedEmptyMultiValueConditionException extends DslParserException {
    public UnexpectedEmptyMultiValueConditionException() {
        super("Multi value condition cannot be empty");
    }
}
