package dk.kvalitetsit.itukt.common.repository;

import dk.kvalitetsit.itukt.common.model.Clause;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClauseCacheTest {

    @Test
    void getClause_WhenClauseIsNotInCache_ReturnsEmptyOptional() {
        Clause existingClause = new Clause("test", null, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause));

        var result = clauseCache.getClause("NonExistentClause");

        assertFalse(result.isPresent());
    }

    @Test
    void getClause_WhenClauseIsInCache_ReturnsClause() {
        Clause existingClause1 = new Clause("test1", null, null);
        Clause existingClause2 = new Clause("test2", null, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause1, existingClause2));

        var result = clauseCache.getClause(existingClause1.name());

        assertTrue(result.isPresent());
        assertEquals(existingClause1, result.get());
    }
}