package dk.kvalitetsit.itukt.validation.stamdata.service.model;


import java.util.Set;

public record DrugClause(Drug drug, Set<Clause> clauses) {
    public record Clause(String code, String text) {
    }

    public record Drug(Long id) {
    }
}
