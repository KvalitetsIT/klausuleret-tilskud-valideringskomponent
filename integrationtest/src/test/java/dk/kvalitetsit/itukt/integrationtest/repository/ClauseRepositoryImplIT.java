package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ClauseRepositoryImplIT extends BaseTest {

    private final ClauseRepositoryImpl repository;

    public ClauseRepositoryImplIT(@Autowired ClauseRepositoryImpl repository) {
        this.repository = repository;
    }

    @Test
    public void testReadAll(){
        var clause_1 = this.repository.create(MockFactory.CLAUSE_1_ENTITY);

        var clauses = this.repository.readAll();

        Assertions.assertEquals(clause_1.get(), clauses.get(0));
    }
}
