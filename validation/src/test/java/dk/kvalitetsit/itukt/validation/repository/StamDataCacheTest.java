package dk.kvalitetsit.itukt.validation.repository;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StamDataCacheTest {

    @Test
    void getClauseCodeByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        StamDataCache stamDataCache = new StamDataCache(Map.of(1L, "clause"));

        var result = stamDataCache.getClauseCodeByDrugId(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void getClauseCodeByDrugId_WhenDrugIdIsInCache_ReturnsClauseName() {
        StamDataCache stamDataCache = new StamDataCache(Map.of(1L, "clause1", 2L, "clause2"));

        var result = stamDataCache.getClauseCodeByDrugId(1L);

        assertTrue(result.isPresent());
        assertEquals("clause1", result.get());
    }
}