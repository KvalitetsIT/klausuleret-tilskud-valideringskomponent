package dk.kvalitetsit.itukt.validation.repository;

public record DrugClauseView(Laegemiddel laegemiddel, Klausulering klausulering) {
    public record Klausulering(String Kode, String Tekst) {

    }

    public record Laegemiddel(Long DrugId) {

    }
}
