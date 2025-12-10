package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StamDataRepositoryIT extends BaseTest {

    private StamDataRepository repository;

    @BeforeAll
    void setup() {
        this.repository = new StamDataRepository(stamDatabase.getDatasource());
    }

    @Test
    void testFindAll() {
        var entries = this.repository.findAll();
        Assertions.assertFalse(entries.isEmpty());
    }


    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }
}
