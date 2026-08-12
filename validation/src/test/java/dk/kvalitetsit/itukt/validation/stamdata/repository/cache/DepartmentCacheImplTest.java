package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepartmentCacheImplTest {

    @Mock
    private Repository<Department> mock;

    @InjectMocks
    private DepartmentCacheImpl cache;

    @Test
    void get_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        Department data = new Department(
                Optional.empty(),
                Optional.of(new Department.Identifier.SOR("very long sor code")),
                Set.of(new Department.Speciality("long clause text"))
        );

        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.run();

        var result = cache.get(new Department.Identifier.SOR("some missing sor code"));

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        Department.Identifier.SOR sor = new Department.Identifier.SOR("very long sor code");
        Department data = new Department(
                Optional.empty(),
                Optional.of(sor),
                Set.of(new Department.Speciality("long clause text"))
        );

        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.run();

        var result = cache.get(sor);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        var sor = new Department.Identifier.SOR("very long sor code");
        Department data = new Department(Optional.empty(), Optional.of(sor), Set.of(new Department.Speciality("long clause text")));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.run();

        Mockito.verify(mock, Mockito.times(1)).fetchAll();
        var result1 = cache.get(sor);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been previously reloaded");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = cache.get(sor);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }

    @Test
    void get_assertCorrectlyMergedSpecialitiesWhenMatchingSorCode() {
        var sor = new Department.Identifier.SOR("very long sor code");

        Department a = new Department(Optional.empty(), Optional.of(sor), Set.of(new Department.Speciality("speciality A")));
        Department b = new Department(Optional.empty(), Optional.of(sor), Set.of(new Department.Speciality("speciality B")));

        Mockito.when(mock.fetchAll()).thenReturn(List.of(a, b));
        cache.run();

        Mockito.verify(mock, Mockito.times(1)).fetchAll();
        var result = cache.get(sor);

        assertTrue(result.isPresent(), "Expected a result since the cache has been previously reloaded");

        Department expected = new Department(Optional.empty(), Optional.of(sor), Set.of(
                new Department.Speciality("speciality A"),
                new Department.Speciality("speciality B")));

        assertEquals(expected, result.get(), "Expected the cache to return a merged set of specialities");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = cache.get(sor);
        assertEquals(result, result2, "Expected the data to be the same as previously returned by the first invocation");
    }

    @Test
    void run_WithNeitherSorOrShak_DoesNotFail() {
        Department department = new Department(Optional.empty(), Optional.empty(), Set.of());

        Mockito.when(mock.fetchAll()).thenReturn(List.of(department));

        assertDoesNotThrow(() -> cache.run());
    }

    @Test
    void getSpecialities_BeforeRun_ReturnsEmptySet() {
        var specialities = cache.getSpecialities();

        assertTrue(specialities.isEmpty(), "Expected getSpecialities to return an empty set before run is called");
    }

    @Test
    void getSpecialities_WhenLoadedDepartmentsHaveNoSpecialities_ReturnsEmptySet() {
        var shak = new Department.Identifier.SHAK("A");
        Department department = new Department(Optional.of(shak), Optional.empty(), Set.of());
        Mockito.when(mock.fetchAll()).thenReturn(List.of(department));
        cache.run();

        var specialities = cache.getSpecialities();

        assertTrue(specialities.isEmpty(), "Expected getSpecialities to return an empty set when loaded departments have no specialities");
    }

    @Test
    void getSpecialities_WhenLoadedDepartmentsHaveMultipleSpecialities_ReturnsAllNonBlankSpecialities() {
        var shak = new Department.Identifier.SHAK("A");
        var sor = new Department.Identifier.SOR("B");
        var specialityA = new Department.Speciality("speciality A");
        var specialityB = new Department.Speciality("speciality B");
        var blankSpeciality = new Department.Speciality("");
        var department1 = new Department(Optional.of(shak), Optional.empty(), Set.of(specialityA));
        var department2 = new Department(Optional.empty(), Optional.of(sor), Set.of(specialityA, specialityB, blankSpeciality));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(department1, department2));
        cache.run();

        var specialities = cache.getSpecialities();

        assertEquals(Set.of(specialityA, specialityB), specialities, "Expected getSpecialities to return all unique specialities from loaded departments");
    }

    @Test
    void getSpeciality_WhenLoadedDepartmentsDoNotContainSpeciality_ReturnsEmpty() {
        var shak = new Department.Identifier.SHAK("A");
        var speciality = new Department.Speciality("speciality A");
        var department = new Department(Optional.of(shak), Optional.empty(), Set.of(speciality));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(department));
        cache.run();

        var result = cache.getSpeciality("non-existent speciality");

        assertTrue(result.isEmpty(), "Expected getSpeciality to return empty when the loaded departments do not contain the speciality");
    }

    @Test
    void getSpeciality_WhenLoadedDepartmentsDoContainSpeciality_ReturnsSpeciality() {
        var shak = new Department.Identifier.SHAK("A");
        var speciality = new Department.Speciality("speciality A");
        var department = new Department(Optional.of(shak), Optional.empty(), Set.of(speciality));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(department));
        cache.run();

        var result = cache.getSpeciality("sPeCiAlItY a");

        assertTrue(result.isPresent(), "Expected getSpeciality to return speciality");
        assertEquals(speciality, result.get(), "Expected getSpeciality to return the correct speciality");
    }
}