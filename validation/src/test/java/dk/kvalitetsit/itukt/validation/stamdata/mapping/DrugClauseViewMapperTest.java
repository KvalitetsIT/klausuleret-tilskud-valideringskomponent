package dk.kvalitetsit.itukt.validation.stamdata.mapping;

import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DrugClauseViewMapper;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class DrugClauseViewMapperTest {

    @Test
    void map_givenAListOf5Entries_whenDrugIdIsRedundant_thenMergeAndReturnMapOfFiveEntries() {

        List<DrugClauseView> given = List.of(
                new DrugClauseView(new DrugClauseView.Laegemiddel(1L), new DrugClauseView.Klausulering("kode1", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(2L), new DrugClauseView.Klausulering("kode2", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(3L), new DrugClauseView.Klausulering("kode3", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(4L), new DrugClauseView.Klausulering("kode4", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(5L), new DrugClauseView.Klausulering("kode5", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(6L), new DrugClauseView.Klausulering("kode6", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(7L), new DrugClauseView.Klausulering("kode7", "tekst"))
        );

        List<DrugClause> expected = List.of(
                new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("kode1", "tekst"))),
                new DrugClause(new DrugClause.Drug(2L), Set.of(new DrugClause.Clause("kode2", "tekst"))),
                new DrugClause(new DrugClause.Drug(3L), Set.of(new DrugClause.Clause("kode3", "tekst"))),
                new DrugClause(new DrugClause.Drug(4L), Set.of(new DrugClause.Clause("kode4", "tekst"))),
                new DrugClause(new DrugClause.Drug(5L), Set.of(new DrugClause.Clause("kode5", "tekst"))),
                new DrugClause(new DrugClause.Drug(6L), Set.of(new DrugClause.Clause("kode6", "tekst"))),
                new DrugClause(new DrugClause.Drug(7L), Set.of(new DrugClause.Clause("kode7", "tekst")))

        );

        // when
        DrugClauseViewMapper mapper = new DrugClauseViewMapper();
        List<DrugClause> map = mapper.map(given);

        // then
        Assertions.assertEquals(expected, map);
    }
}
