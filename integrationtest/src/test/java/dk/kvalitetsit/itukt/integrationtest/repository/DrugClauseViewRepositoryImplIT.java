package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.repository.DrugClauseViewRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DrugClauseViewRepositoryImplIT extends BaseTest {

    private DrugClauseViewRepositoryImpl repository;

    @BeforeAll
    void setup() {
        this.repository = new DrugClauseViewRepositoryImpl(stamDatabase.getDatasource());
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
