package dk.kvalitetsit.itukt.common.model;


public record ValidationError(String error, boolean isOrError) {

    public ValidationError(String error) { this(error, false); }

    public String toErrorString() { return error; }
}