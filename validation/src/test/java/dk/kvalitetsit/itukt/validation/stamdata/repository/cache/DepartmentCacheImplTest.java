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
                null,
                new Department.Identifier.SOR("very long sor code"),
                Set.of(new Department.Speciality("long clause text"))
        );

        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(new Department.Identifier.SOR("some missing sor code"));

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        Department data = new Department(
                null,
                new Department.Identifier.SOR("very long sor code"),
                Set.of(new Department.Speciality("long clause text"))
        );

        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(data.sor());

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        var sor = new Department.Identifier.SOR("very long sor code");
        Department data = new Department(null, sor, Set.of(new Department.Speciality("long clause text")));
        Mockito.when(mock.fetchAll()).thenReturn(List.of(data));
        cache.load();

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

        Department a = new Department(null, sor, Set.of(new Department.Speciality("speciality A")));
        Department b = new Department(null, sor, Set.of(new Department.Speciality("speciality B")));

        Mockito.when(mock.fetchAll()).thenReturn(List.of(a, b));
        cache.load();

        Mockito.verify(mock, Mockito.times(1)).fetchAll();
        var result = cache.get(sor);

        assertTrue(result.isPresent(), "Expected a result since the cache has been previously reloaded");

        Department expected = new Department(null, sor, Set.of(
                new Department.Speciality("speciality A"),
                new Department.Speciality("speciality B")));

        assertEquals(expected, result.get(), "Expected the cache to return a merged set of specialities");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = cache.get(sor);
        assertEquals(result, result2, "Expected the data to be the same as previously returned by the first invocation");
    }

}