package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

            ClauseEntity written_clause = written.get(i);
            ClauseEntity read_clause = read.get(i);

            Assertions.assertNotEquals(clause.uuid(), written_clause.uuid());
            Assertions.assertNotEquals(clause.uuid(), read_clause.uuid());

            assertThat(written_clause)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "uuid", "expression.id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                    .isEqualTo(clause);

            assertThat(read_clause)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "uuid", "expression.id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                    .isEqualTo(clause);
        }
    }
}
