package dk.kvalitetsit.itukt.validation.service.model;


import java.util.Set;

public record StamData(Drug drug, Set<Clause> clauses) {
    public record Clause(String code, String text) {
    }

    public record Drug(Long id) {
    }
}
