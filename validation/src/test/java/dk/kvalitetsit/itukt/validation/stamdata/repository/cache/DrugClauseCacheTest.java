package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DrugClauseCacheTest {

    @Mock
    private DrugClauseViewRepositoryAdaptor mock;

    @Test
    void getStamDataByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        long drugId = 1L;
        DrugClause data = new DrugClause(new DrugClause.Drug(drugId), Set.of(new DrugClause.Clause("code", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        DrugClauseCacheImpl stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.get(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        DrugClause data = new DrugClause(new DrugClause.Drug(drugId), Set.of(new DrugClause.Clause("code", "long clause text")));

        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        DrugClauseCacheImpl stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.get(drugId);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        long drugId = 1L;
        DrugClause data = new DrugClause(new DrugClause.Drug(drugId), Set.of(new DrugClause.Clause("code", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(Map.of(drugId, data));
        DrugClauseCacheImpl stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        Mockito.verify(mock, Mockito.times(1)).findAll();
        var result1 = stamDataCache.get(drugId);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = stamDataCache.get(drugId);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }
}