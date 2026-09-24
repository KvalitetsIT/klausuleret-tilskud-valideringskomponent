package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DoctorSpecialityCacheTest {
    @Mock
    private CacheConfiguration configuration;

    @Mock
    private Repository<DoctorSpeciality> repository;

    @InjectMocks
    private DoctorSpecialityCache doctorSpecialityCache;

    @Test
    void getAll_BeforeRun_ReturnsEmptySet() {
        assertTrue(doctorSpecialityCache.getAll().isEmpty());
    }

    @Test
    void getAll_AfterRun_ReturnsDistinctSpecialities() {
        var speciality1 = new DoctorSpeciality("specialityA");
        var speciality2 = new DoctorSpeciality("specialityB");
        var speciality3 = new DoctorSpeciality("specialityB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(speciality1, speciality2, speciality3));

        doctorSpecialityCache.run();
        var specialities = doctorSpecialityCache.getAll();

        var expected = Set.of(speciality1, speciality2);
        assertEquals(expected, specialities);
    }

    @Test
    void get_NotMatchingSpecialityFromLoad_ReturnsEmpty() {
        var speciality1 = new DoctorSpeciality("specialityA");
        var speciality2 = new DoctorSpeciality("specialityB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(speciality1, speciality2));

        doctorSpecialityCache.run();
        var result = doctorSpecialityCache.get("nonExistingSpeciality");

        assertTrue(result.isEmpty());
    }

    @Test
    void get_MatchingSpecialityFromLoad_ReturnsSpeciality() {
        var speciality1 = new DoctorSpeciality("specialityA");
        var speciality2 = new DoctorSpeciality("specialityB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(speciality1, speciality2));

        doctorSpecialityCache.run();
        var result = doctorSpecialityCache.get(speciality1.value());

        assertTrue(result.isPresent());
        assertEquals(speciality1, result.get());
    }

    @Test
    void get_BeforeRun_ReturnsEmpty() {
        assertTrue(doctorSpecialityCache.get("someSpeciality").isEmpty());
    }
}