package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.validation.repository.DrugClauseView;
import dk.kvalitetsit.itukt.validation.service.model.DrugClause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class DrugClauseViewMapperTest {

    @Test
    void map_givenAListOf5Entries_whenDrugIdIsRedundant_thenMergeAndReturnMapOfFiveEntries() {

        List<DrugClauseView> given = List.of(
                new DrugClauseView(new DrugClauseView.Laegemiddel(1L), new DrugClauseView.Klausulering("kode1", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(2L), new DrugClauseView.Klausulering("kode2", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(2L), new DrugClauseView.Klausulering("kode3", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(4L), new DrugClauseView.Klausulering("kode4", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(5L), new DrugClauseView.Klausulering("kode5", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(5L), new DrugClauseView.Klausulering("kode6", "tekst")),
                new DrugClauseView(new DrugClauseView.Laegemiddel(5L), new DrugClauseView.Klausulering("kode7", "tekst"))
        );


        Map<Long, DrugClause> expected = Map.of(
                1L, new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("kode1", "tekst"))),
                2L, new DrugClause(new DrugClause.Drug(2L), Set.of(
                        new DrugClause.Clause("kode2", "tekst"),
                        new DrugClause.Clause("kode3", "tekst")
                )),
                4L, new DrugClause(new DrugClause.Drug(4L), Set.of(new DrugClause.Clause("kode4", "tekst"))),
                5L, new DrugClause(new DrugClause.Drug(5L), Set.of(
                        new DrugClause.Clause("kode5", "tekst"),
                        new DrugClause.Clause("kode6", "tekst"),
                        new DrugClause.Clause("kode7", "tekst")
                ))
        );

        // when
        DrugClauseViewMapper mapper = new DrugClauseViewMapper();
        Map<Long, DrugClause> map = mapper.map(given);

        // then
        Assertions.assertEquals(expected, map);
    }
}
