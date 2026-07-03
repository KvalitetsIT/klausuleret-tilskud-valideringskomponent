package dk.kvalitetsit.itukt.management.exceptions;

public final class IncompleteDslException extends DslParserException {
    public IncompleteDslException() {
        super("Incomplete dsl");
    }
}
