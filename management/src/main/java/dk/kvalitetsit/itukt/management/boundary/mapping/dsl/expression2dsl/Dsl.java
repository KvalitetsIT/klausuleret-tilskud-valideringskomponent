package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

record Dsl(String dsl, Type type) {
    enum Type {
        CONDITION,
        OR,
        AND,
    }
}