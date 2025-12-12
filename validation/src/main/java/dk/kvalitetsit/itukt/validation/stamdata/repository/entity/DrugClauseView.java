package dk.kvalitetsit.itukt.validation.stamdata.repository.entity;

public record DrugClauseView(Laegemiddel laegemiddel, Klausulering klausulering) {
    public record Klausulering(String Kode, String Tekst) {

    }

    public record Laegemiddel(Long DrugId) {

    }
}
