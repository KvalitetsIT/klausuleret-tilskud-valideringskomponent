package dk.kvalitetsit.itukt.validation.stamdata.repository.entity;

/**
 * The record has been named view since it is not a direct reflection of a table row like entities but a selection from multiple tables
 * @param laegemiddel a representation of the "Laegemiddel" table in the database
 * @param klausulering a representation of the "Klausulering" table in the database
 */
public record DrugClauseView(Laegemiddel laegemiddel, Klausulering klausulering) {
    public record Klausulering(String Kode, String Tekst) {

    }

    public record Laegemiddel(Long DrugId) {

    }
}
