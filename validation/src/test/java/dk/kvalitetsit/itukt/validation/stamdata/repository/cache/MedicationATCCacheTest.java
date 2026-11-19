package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MedicationATCCacheTest {
    @Mock
    private CacheConfiguration configuration;

    @Mock
    private Repository<Medication.ATC> repository;

    @InjectMocks
    private MedicationATCCache atcCache;

    @Test
    void getAll_BeforeRun_ReturnsEmptySet() {
        assertTrue(atcCache.getAll().isEmpty());
    }

    @Test
    void getAll_AfterRun_ReturnsDistinctATCs() {
        var atc1 = new Medication.ATC("atcA");
        var atc2 = new Medication.ATC("atcB");
        var atc3 = new Medication.ATC("atcB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(atc1, atc2, atc3));

        atcCache.run();
        var atcs = atcCache.getAll();

        var expected = Set.of(atc1, atc2);
        assertEquals(expected, atcs);
    }

    @Test
    void get_NotMatchingATCFromLoad_ReturnsEmpty() {
        var atc1 = new Medication.ATC("atcA");
        var atc2 = new Medication.ATC("atcB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(atc1, atc2));

        atcCache.run();
        var result = atcCache.get("nonExistingATCCode");

        assertTrue(result.isEmpty());
    }

    @Test
    void get_MatchingATCFromLoad_ReturnsATC() {
        var atc1 = new Medication.ATC("atcA");
        var atc2 = new Medication.ATC("atcB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(atc1, atc2));

        atcCache.run();
        var result = atcCache.get(atc1.code());

        assertTrue(result.isPresent());
        assertEquals(atc1, result.get());
    }

    @Test
    void get_BeforeRun_ReturnsEmpty() {
        assertTrue(atcCache.get("someATCCode").isEmpty());
    }
}