package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DepartmentRepository;
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
    void testFetchAll() {
        var entries = this.repository.fetchAll();
        Assertions.assertFalse(entries.isEmpty());
    }


}
