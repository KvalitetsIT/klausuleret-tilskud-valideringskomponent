package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.FormbetegnelseRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.MedicationFormRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicationFormRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private MedicationFormRepository repository;
    private FormbetegnelseRepository formbetegnelseRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        formbetegnelseRepository = new FormbetegnelseRepository(datasource);
        repository = new MedicationFormRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @Test
    void fetchAll_WithNoFormCodesInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidFormCodesInDB_ReturnsValidFormCodes() {
        var valid1 = new Medication.Form("VALID1");
        var valid2 = new Medication.Form("VALID2");
        var invalid1 = new Medication.Form("INVALID1");
        var invalid2 = new Medication.Form("INVALID2");
        formbetegnelseRepository.insert(valid1, inThePast, inTheFuture);
        formbetegnelseRepository.insert(valid2, inThePast, inTheFuture);
        formbetegnelseRepository.insert(invalid1, inThePast, inThePast);
        formbetegnelseRepository.insert(invalid2, inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(valid1));
        assertTrue(entries.contains(valid2));
    }
}
