package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.Autorisation3Repository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DoctorSpecialityRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoctorSpecialityRepositoryIT extends BaseTest {
    private static Date inThePast, inTheFuture;
    private DoctorSpecialityRepository repository;
    private Autorisation3Repository autorisationRepository;

    @BeforeAll
    void setup() {
        var datasource = stamDatabase.getDatasource();
        autorisationRepository = new Autorisation3Repository(datasource);
        repository = new DoctorSpecialityRepository(datasource);

        inThePast = Date.from(Instant.now().minusSeconds(1));
        inTheFuture = Date.from(Instant.now().plusSeconds(1000));
    }

    @Test
    void fetchAll_WithNoSpecialityInDB_ReturnsEmptyList() {
        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithEmptySpecialitiesInDB_ReturnsEmptyList() {
        var emptySpeciality = Optional.of(new DoctorSpeciality(""));
        autorisationRepository.insert(Optional.empty(), emptySpeciality, emptySpeciality, inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void fetchAll_WithBothValidAndInvalidSpecialityInDB_ReturnsValidSpeciality() {
        var valid1 = new DoctorSpeciality("VALID1");
        var valid2 = new DoctorSpeciality("VALID2");
        var valid3 = new DoctorSpeciality("VALID3");
        var invalid1 = new DoctorSpeciality("INVALID1");
        var invalid2 = new DoctorSpeciality("INVALID2");
        var invalid3 = new DoctorSpeciality("INVALID3");
        autorisationRepository.insert(Optional.of(valid1), Optional.of(valid2), Optional.of(valid3), inThePast, inTheFuture);
        autorisationRepository.insert(Optional.of(invalid1), Optional.of(invalid2), Optional.of(invalid3), inThePast, inThePast);
        autorisationRepository.insert(Optional.of(invalid1), Optional.of(invalid2), Optional.of(invalid3), inTheFuture, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(3, entries.size());
        assertTrue(entries.contains(valid1));
        assertTrue(entries.contains(valid2));
        assertTrue(entries.contains(valid3));
    }

    @Test
    void fetchAll_WithDuplicateSpecialities_ReturnsDistinctSpecialities() {
        var valid = new DoctorSpeciality("VALID");
        autorisationRepository.insert(Optional.of(valid), Optional.of(valid), Optional.of(valid), inThePast, inTheFuture);
        autorisationRepository.insert(Optional.of(valid), Optional.of(valid), Optional.of(valid), inThePast, inTheFuture);

        var entries = this.repository.fetchAll();

        assertEquals(1, entries.size());
        assertEquals(valid, entries.getFirst());
    }
}
