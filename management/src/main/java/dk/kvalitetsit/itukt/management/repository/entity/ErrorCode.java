package dk.kvalitetsit.itukt.management.repository.entity;

public record ErrorCode(int code) {
    public ErrorCode {
        if (code > 10999) throw new IllegalStateException(String.format(
                "The error code (%s) must be less than 11000",
                code
        ));
        if (code < 10800) throw new IllegalStateException(String.format(
                "The error code (%s) must be greater than 10800",
                code
        ));
    }
}
