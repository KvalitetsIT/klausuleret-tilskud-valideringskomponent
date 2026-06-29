package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        DrugClauseCacheImpl stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.get(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        DrugClause data = new DrugClause(new DrugClause.Drug(drugId), Set.of(new DrugClause.Clause("code", "long clause text")));

        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
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
        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        DrugClauseCacheImpl stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        Mockito.verify(mock, Mockito.times(1)).fetchAll();
        var result1 = stamDataCache.get(drugId);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = stamDataCache.get(drugId);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }

    @Test
    void getNumberOfDrugsForClause_BeforeLoadIsCalled_Returns0() {
        var stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);

        var result = stamDataCache.getNumberOfDrugsForClause("test");

        assertEquals(0, result);
    }

    @Test
    void getNumberOfDrugsForClause_WhenClauseIsNotInCache_Returns0() {
        var drugClause = new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("code", "")));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(drugClause));
        var stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.getNumberOfDrugsForClause("non-existing-code");

        assertEquals(0, result);
    }

    @Test
    void getNumberOfDrugsForClause_WhenClauseHas2DifferentDrugs_Returns2() {
        var clause = new DrugClause.Clause("test", "");
        var drugClause1 = new DrugClause(new DrugClause.Drug(1L), Set.of(clause));
        var drugClause2 = new DrugClause(new DrugClause.Drug(2L), Set.of(clause));
        var drugClause3 = new DrugClause(new DrugClause.Drug(3L), Set.of(new DrugClause.Clause("another-clause", "")));
        var drugClause4 = new DrugClause(new DrugClause.Drug(2L), Set.of(clause));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(drugClause1, drugClause2, drugClause3, drugClause4));
        var stamDataCache = new DrugClauseCacheImpl(new CacheConfiguration(""), mock);
        stamDataCache.load();

        var result = stamDataCache.getNumberOfDrugsForClause(clause.code());

        assertEquals(2, result);
    }
}