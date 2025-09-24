package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.entity.StamData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
        stamDataCache.init();

        var result = stamDataCache.getStamDataByDrugId(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), List.of(new StamData.Clause("clauseCode", "long clause text")));

        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        StamDataCache stamDataCache = new StamDataCache(new CacheConfiguration(""), mock);
        stamDataCache.init();

        var result = stamDataCache.getStamDataByDrugId(drugId);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }
}