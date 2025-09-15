package dk.kvalitetsit.itukt.common.model;


import java.util.Set;

public record StamData(Drug drug, Set<Clause> clauses) {
    public record Clause(String code, String text) {}

    public record Drug(Long id) { }
}
