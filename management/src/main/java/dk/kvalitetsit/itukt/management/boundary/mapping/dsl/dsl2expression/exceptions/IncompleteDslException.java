package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

public final class IncompleteDslException extends DslParserException {
    public IncompleteDslException() {
        super("Incomplete dsl");
    }
}
