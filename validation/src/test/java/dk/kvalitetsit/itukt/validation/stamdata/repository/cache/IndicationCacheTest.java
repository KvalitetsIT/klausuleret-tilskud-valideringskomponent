package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Indication;
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
class IndicationCacheTest {
    @Mock
    private CacheConfiguration configuration;

    @Mock
    private Repository<Indication> repository;

    @InjectMocks
    private IndicationCache indicationCache;

    @Test
    void getAll_BeforeRun_ReturnsEmptySet() {
        assertTrue(indicationCache.getAll().isEmpty());
    }

    @Test
    void getAll_AfterRun_ReturnsDistinctIndications() {
        var indication1 = new Indication(1);
        var indication2 = new Indication(2);
        var indication3 = new Indication(2);

        Mockito.when(repository.fetchAll()).thenReturn(List.of(indication1, indication2, indication3));

        indicationCache.run();
        var indications = indicationCache.getAll();

        var expected = Set.of(indication1, indication2);
        assertEquals(expected, indications);
    }

    @Test
    void get_NotMatchingIndicationFromLoad_ReturnsEmpty() {
        var indication1 = new Indication(1);
        var indication2 = new Indication(2);

        Mockito.when(repository.fetchAll()).thenReturn(List.of(indication1, indication2));

        indicationCache.run();
        var result = indicationCache.get("nonExistingIndicationCode");

        assertTrue(result.isEmpty());
    }

    @Test
    void get_MatchingIndicationFromLoad_ReturnsIndication() {
        var indication1 = new Indication(1);
        var indication2 = new Indication(2);

        Mockito.when(repository.fetchAll()).thenReturn(List.of(indication1, indication2));

        indicationCache.run();
        var result = indicationCache.get("1");

        assertTrue(result.isPresent());
        assertEquals(indication1, result.get());
    }

    @Test
    void get_BeforeRun_ReturnsEmpty() {
        assertTrue(indicationCache.get("someIndicationCode").isEmpty());
    }
}