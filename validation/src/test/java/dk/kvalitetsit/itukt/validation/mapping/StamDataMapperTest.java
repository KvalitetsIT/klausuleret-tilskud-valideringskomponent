package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.validation.repository.entity.StamDataEntity;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class StamDataMapperTest {

    @Test
    void map_givenAListOf5Entries_whenDrugIdIsRedundant_thenMergeAndReturnMapOfFiveEntries() {

        List<StamDataEntity> given = List.of(
                new StamDataEntity(1L, "kode1", "tekst"),
                new StamDataEntity(2L, "kode2", "tekst"),
                new StamDataEntity(2L, "kode3", "tekst"),
                new StamDataEntity(4L, "kode4", "tekst"),
                new StamDataEntity(5L, "kode5", "tekst"),
                new StamDataEntity(5L, "kode6", "tekst"),
                new StamDataEntity(5L, "kode7", "tekst")
        );


        Map<Long, StamData> expected = Map.of(
                1L, new StamData(new StamData.Drug(1L), Set.of(new StamData.Clause("kode1", "tekst"))),
                2L, new StamData(new StamData.Drug(2L), Set.of(
                        new StamData.Clause("kode2", "tekst"),
                        new StamData.Clause("kode3", "tekst")
                )),
                4L, new StamData(new StamData.Drug(4L), Set.of(new StamData.Clause("kode4", "tekst"))),
                5L, new StamData(new StamData.Drug(5L), Set.of(
                        new StamData.Clause("kode5", "tekst"),
                        new StamData.Clause("kode6", "tekst"),
                        new StamData.Clause("kode7", "tekst")
                ))
        );

        // when
        StamDataMapper mapper = new StamDataMapper();
        Map<Long, StamData> map = mapper.map(given);

        // then
        Assertions.assertEquals(expected, map);
    }
}
