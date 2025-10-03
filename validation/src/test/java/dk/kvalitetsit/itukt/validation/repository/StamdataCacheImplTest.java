package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCache;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCacheImpl;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StamDataCacheTest {

    @Mock
    private StamDataRepositoryAdaptor mock;

    @Test
    void getStamDataByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("clauseCode", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        StamdataCacheImpl stamDataCache = new StamdataCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.get(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("clauseCode", "long clause text")));

        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        StamdataCacheImpl stamDataCache = new StamdataCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.get(drugId);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("clauseCode", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        StamdataCache stamDataCache = new StamdataCacheImpl(new CacheConfiguration(""), mock);
        CacheLoader loader = (CacheLoader) stamDataCache;
        loader.load();

        Mockito.verify(mock, Mockito.times(1)).findAll();
        var result1 = stamDataCache.get(drugId);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = stamDataCache.get(drugId);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }
}