package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.repository.DepartmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DepartmentRepositoryIT extends BaseTest {

    private DepartmentRepository repository;

    @BeforeAll
    void setup() {
        this.repository = new DepartmentRepository(stamDatabase.getDatasource());
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void testFindAll() {
        var entries = this.repository.findAll();
        Assertions.assertFalse(entries.isEmpty());
    }


}
