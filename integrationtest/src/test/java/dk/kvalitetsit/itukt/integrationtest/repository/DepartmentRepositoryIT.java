package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.SorEntityRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DepartmentRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DepartmentRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private DepartmentRepository repository;
    private JdbcTemplate jdbcTemplate;
    private SorEntityRepository sorEntityRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        jdbcTemplate = new JdbcTemplate(stamDatabase.getDatasource());
        sorEntityRepository = new SorEntityRepository(datasource);
        repository = new DepartmentRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM SorEntity");
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void fetchAll_WhenSorEntityIsNotValidAnymore_ReturnsEmptyList() {
        var sorEntity = new DepartmentEntity("1", "shak", "spec", null, null, null, null, null, null, null);
        sorEntityRepository.insert(sorEntity, null, null, inThePast, inThePast);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WhenToDateIsInThePast_ReturnsEmptyList() {
        var sorEntity = new DepartmentEntity("1", "shak", "spec", null, null, null, null, null, null, null);
        sorEntityRepository.insert(sorEntity, null, inThePast, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WhenSorEntityIsNotValidYet_ReturnsEmptyList() {
        var sorEntity = new DepartmentEntity("1", "shak", "spec", null, null, null, null, null, null, null);
        sorEntityRepository.insert(sorEntity, null, null, inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WhenFromDateIsInTheFuture_ReturnsEmptyList() {
        var sorEntity = new DepartmentEntity("1", "shak", "spec", null, null, null, null, null, null, null);
        sorEntityRepository.insert(sorEntity, inTheFuture, null, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WhenEntityHaveNoSpeciality_ReturnsEmptyList() {
        var sorEntity = new DepartmentEntity("1", "shak", null, null, null, null, null, null, null, null);
        sorEntityRepository.insert(sorEntity, null, null, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithNullValuesExceptOneSpeciality_ReturnsEntities() {
        var sorEntity1 = new DepartmentEntity(null, null, null, null, null, "spec", null, null, null, null);
        var sorEntity2 = new DepartmentEntity(null, null, null, null, null, null, null, null, null, "spec");
        sorEntityRepository.insert(sorEntity1, null, null, inThePast, inTheFuture);
        sorEntityRepository.insert(sorEntity2, null, null, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(sorEntity1));
        assertTrue(entries.contains(sorEntity2));
    }

    @Test
    void fetchAll_WithAllValuesSet_ReturnsEntities() {
        var sorEntity1 = new DepartmentEntity("1", "shak1", "spec1", "spec2", "spec3", "spec4", "spec5", "spec6", "spec7", "spec8");
        var sorEntity2 = new DepartmentEntity("2", "shak2", "spec1", "spec2", "spec3", "spec4", "spec5", "spec6", "spec7", "spec8");
        var sorEntity3 = new DepartmentEntity("2", "shak2", "a", "b", "c", "d", "e", "f", "g", "h");
        sorEntityRepository.insert(sorEntity1, inThePast, inTheFuture, inThePast, inTheFuture);
        sorEntityRepository.insert(sorEntity2, inThePast, inTheFuture, inThePast, inTheFuture);
        sorEntityRepository.insert(sorEntity3, inThePast, inTheFuture, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(3, entries.size());
        assertTrue(entries.contains(sorEntity1));
        assertTrue(entries.contains(sorEntity2));
        assertTrue(entries.contains(sorEntity3));
    }


}
