package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClauseRepositoryImplIT extends BaseTest {

    private final ClauseRepositoryImpl repository;

    ClauseRepositoryImplIT(@Autowired ClauseRepositoryImpl repository) {
        this.repository = repository;
    }

    @Test
    void testReadAll() {

        var clauses = List.of(MockFactory.CLAUSE_1_ENTITY, MockFactory.CLAUSE_1_ENTITY);

        var written = clauses.stream().map(this.repository::create).toList();
        var read = this.repository.readAll();
        assertEquals(clauses.size(), read.size());
        for (int i = 0; i < written.size(); i++) {

            ClauseEntity clause = clauses.get(i);

            ClauseEntity writtenClause = written.get(i);
            ClauseEntity readClause = read.get(i);

            Assertions.assertNotEquals(clause.uuid(), writtenClause.uuid(), "The uuid of the given clause is expected to be replaced");
            Assertions.assertNotEquals(clause.uuid(), readClause.uuid(), "The uuid of the given clause is expected to be replaced");
            assertThat(readClause)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "uuid", "expression.id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                    .isEqualTo(clause);
        assertEquals(writtenClause, readClause, "The clause read from the database is expected to match the one written beforehand");
    }
}
}
