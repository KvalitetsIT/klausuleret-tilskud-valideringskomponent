package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.entity.StamData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StamDataCacheTest {

    @Mock
    private StamDataRepository mock;

    @Test
    void getStamDataByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        StamData data = new StamData(new StamData.Drug(1L), List.of(new StamData.Clause("clauseCode", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        StamDataCache stamDataCache = new StamDataCache(new CacheConfiguration(""), mock);
        stamDataCache.reload();

        var result = stamDataCache.getStamDataByDrugId(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), List.of(new StamData.Clause("clauseCode", "long clause text")));

        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        StamDataCache stamDataCache = new StamDataCache(new CacheConfiguration(""), mock);
        stamDataCache.reload();

        var result = stamDataCache.getStamDataByDrugId(drugId);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), List.of(new StamData.Clause("clauseCode", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        StamDataCache stamDataCache = new StamDataCache(new CacheConfiguration(""), mock);
        stamDataCache.reload(); // <- Invokes the mock once
        Mockito.verify(mock, Mockito.times(1)).findAll();
        var result1 = stamDataCache.getStamDataByDrugId(drugId);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = stamDataCache.getStamDataByDrugId(drugId);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }

}