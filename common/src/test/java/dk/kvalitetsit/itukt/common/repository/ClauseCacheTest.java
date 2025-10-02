package dk.kvalitetsit.itukt.common.repository;

import dk.kvalitetsit.itukt.common.model.Clause;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClauseCacheTest {

    @Test
    void getClause_WhenClauseIsNotInCache_ReturnsEmptyOptional() {
        Clause existingClause = new Clause(1L, "test", null, 1, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause));

        var result = clauseCache.getClause("NonExistentClause");

        assertFalse(result.isPresent());
    }

    @Test
    void getClause_WhenClauseIsInCache_ReturnsClause() {
        Clause existingClause1 = new Clause(1L, "test1", null, 1, null);
        Clause existingClause2 = new Clause(2L, "test2", null, 2, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause1, existingClause2));

        var result = clauseCache.getClause(existingClause1.name());

        assertTrue(result.isPresent());
        assertEquals(existingClause1, result.get());
    }

    @Test
    void getByErrorCode_WhenNoClauseMatchesErrorCode_ReturnsEmptyOptional() {
        Clause existingClause = new Clause(null, "test", null, 111, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause));

        var result = clauseCache.getByErrorCode(999);

        assertFalse(result.isPresent(), "Expected empty Optional when no clause matches the error code");
    }

    @Test
    void getByErrorCode_WhenClauseMatchesErrorCode_ReturnsClause() {
        Clause existingClause1 = new Clause(null, "test1", null, 111, null);
        Clause existingClause2 = new Clause(null, "test2", null, 222, null);
        ClauseCache clauseCache = new ClauseCache(List.of(existingClause1, existingClause2));

        var result = clauseCache.getByErrorCode(existingClause2.errorCode());

        assertTrue(result.isPresent(), "Expected clause to be found for matching error code");
        assertEquals(existingClause2, result.get(), "Expected returned clause to match the one with the given error code");
    }
}