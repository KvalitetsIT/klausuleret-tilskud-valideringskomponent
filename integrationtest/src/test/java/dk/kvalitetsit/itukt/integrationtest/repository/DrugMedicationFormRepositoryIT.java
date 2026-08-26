package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.DrugMedication;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.FormbetegnelseRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugMedicationFormRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrugMedicationFormRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private DrugMedicationFormRepository repository;
    private JdbcTemplate jdbcTemplate;
    private FormbetegnelseRepository formbetegnelseRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        jdbcTemplate = new JdbcTemplate(stamDatabase.getDatasource());
        formbetegnelseRepository = new FormbetegnelseRepository(datasource);
        repository = new DrugMedicationFormRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM Formbetegnelse");
    }

    @Test
    void fetchAll_WithNoFormCodesInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidFormCodesInDB_ReturnsValidFormCodes() {
        var valid1 = new DrugMedication.Form("VALID1");
        var valid2 = new DrugMedication.Form("VALID2");
        var invalid1 = new DrugMedication.Form("INVALID1");
        var invalid2 = new DrugMedication.Form("INVALID2");
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
