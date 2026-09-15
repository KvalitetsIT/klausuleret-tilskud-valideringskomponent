package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.ATCRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.MedicationATCRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicationATCRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private MedicationATCRepository repository;
    private ATCRepository atcRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        atcRepository = new ATCRepository(datasource);
        repository = new MedicationATCRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @Test
    void fetchAll_WithNoATCInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidATCInDB_ReturnsValidATC() {
        var valid1 = new Medication.ATC("VALID1");
        var valid2 = new Medication.ATC("VALID2");
        var invalid1 = new Medication.ATC("INVALID1");
        var invalid2 = new Medication.ATC("INVALID2");
        atcRepository.insert(valid1, inThePast, inTheFuture);
        atcRepository.insert(valid2, inThePast, inTheFuture);
        atcRepository.insert(invalid1, inThePast, inThePast);
        atcRepository.insert(invalid2, inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(valid1));
        assertTrue(entries.contains(valid2));
    }
}
