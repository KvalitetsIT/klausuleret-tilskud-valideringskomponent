package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.AdministrationsvejRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.MedicationRouteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicationRouteRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private MedicationRouteRepository repository;
    private AdministrationsvejRepository administrationsvejRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        administrationsvejRepository = new AdministrationsvejRepository(datasource);
        repository = new MedicationRouteRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @Test
    void fetchAll_WithNoRouteInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidRouteInDB_ReturnsValidRoute() {
        var valid1 = new Medication.Route("R1");
        var valid2 = new Medication.Route("R2");
        var invalid1 = new Medication.Route("R3");
        var invalid2 = new Medication.Route("R4");
        administrationsvejRepository.insert(valid1, inThePast, inTheFuture);
        administrationsvejRepository.insert(valid2, inThePast, inTheFuture);
        administrationsvejRepository.insert(invalid1, inThePast, inThePast);
        administrationsvejRepository.insert(invalid2, inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(valid1));
        assertTrue(entries.contains(valid2));
    }
}
