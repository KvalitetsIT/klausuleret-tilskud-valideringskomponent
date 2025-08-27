package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ClauseRepositoryImplIT extends BaseTest {

    private final ClauseRepositoryImpl repository;

    ClauseRepositoryImplIT(@Autowired ClauseRepositoryImpl repository) {
        this.repository = repository;
    }

    @Test
    void testReadAll(){
        var clause_1 = this.repository.create(MockFactory.CLAUSE_1_ENTITY);

        var clauses = this.repository.readAll();
        Assertions.assertNotEquals(MockFactory.CLAUSE_1_ENTITY.uuid(), clause_1.get().uuid());
        Assertions.assertEquals(clauses.get(0).uuid(), clause_1.get().uuid());

        Assertions.assertEquals(clause_1.get(), clauses.get(0));
    }
}
