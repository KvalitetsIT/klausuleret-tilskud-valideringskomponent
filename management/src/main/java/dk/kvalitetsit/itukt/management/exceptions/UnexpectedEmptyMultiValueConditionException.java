package dk.kvalitetsit.itukt.management.exceptions;

public final class UnexpectedEmptyMultiValueConditionException extends DslParserException {
    public UnexpectedEmptyMultiValueConditionException() {
        super("Multi value condition cannot be empty");
    }
}
