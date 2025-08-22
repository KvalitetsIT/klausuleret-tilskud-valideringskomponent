package dk.kvalitetsit.itukt.validation.repository;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StamDataCacheImplTest {

    @Test
    void getClauseNameByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        StamDataCacheImpl stamDataCache = new StamDataCacheImpl(Map.of(1L, "clause"));

        var result = stamDataCache.getClauseNameByDrugId(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void getClauseNameByDrugId_WhenDrugIdIsInCache_ReturnsClauseName() {
        StamDataCacheImpl stamDataCache = new StamDataCacheImpl(Map.of(1L, "clause1", 2L, "clause2"));

        var result = stamDataCache.getClauseNameByDrugId(1L);

        assertTrue(result.isPresent());
        assertEquals("clause1", result.get());
    }
}