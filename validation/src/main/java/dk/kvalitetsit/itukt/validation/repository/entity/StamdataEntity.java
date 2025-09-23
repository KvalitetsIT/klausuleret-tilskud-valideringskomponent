package dk.kvalitetsit.itukt.validation.repository.entity;


import java.util.List;

public record StamdataEntity(Drug drug, List<Clause> clause) {
    public record Clause(String code, String text) {}

    public record Drug(Long id) { }
}
