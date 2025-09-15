package dk.kvalitetsit.itukt.management.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ClauseCacheTest {

    @Mock
    private ClauseRepositoryImpl concreteRepository;



//    @Test
//    void getClause_WhenClauseIsNotInCache_ReturnsEmptyOptional() {
//        Clause existingClause = new Clause("test", null, null);
//        ClauseCache clauseCache = new ClauseCacheImpl(List.of(existingClause));
//
//        var result = clauseCache.getClause("NonExistentClause");
//
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void getClause_WhenClauseIsInCache_ReturnsClause() {
//        Clause existingClause1 = new Clause("test1", null, null);
//        Clause existingClause2 = new Clause("test2", null, null);
//
//        ClauseCache clauseCache = new ClauseCache(List.of(existingClause1, existingClause2));
//
//        var result = clauseCache.getClause(existingClause1.name());
//
//        assertTrue(result.isPresent());
//        assertEquals(existingClause1, result.get());
//    }
}