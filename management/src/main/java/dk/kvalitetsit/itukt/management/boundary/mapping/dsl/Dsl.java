package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

public record Dsl(String dsl, Type type) {
    public enum Type {
        CONDITION,
        OR,
        AND,
    }
}