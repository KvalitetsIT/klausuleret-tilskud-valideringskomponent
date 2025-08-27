package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StamDataCacheTest {

    @Test
    void getClauseByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        ClauseEntity clause = Mockito.mock(ClauseEntity.class);
        StamDataCache stamDataCache = new StamDataCache(Map.of(1L, clause));

        var result = stamDataCache.getClauseByDrugId(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsClauseName() {
        ClauseEntity clause1 = Mockito.mock(ClauseEntity.class);
        ClauseEntity clause2 = Mockito.mock(ClauseEntity.class);
        StamDataCache stamDataCache = new StamDataCache(Map.of(1L, clause1, 2L, clause2));

        var result = stamDataCache.getClauseByDrugId(1L);

        assertTrue(result.isPresent());
        assertEquals(clause1, result.get());
    }
}