package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.repository.cache.DepartmentCache;
import dk.kvalitetsit.itukt.validation.service.model.Department;
import dk.kvalitetsit.itukt.validation.service.model.DepartmentIdentifier;
import dk.kvalitetsit.itukt.validation.service.model.Speciality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepartmentCacheTest {

    @Mock
    private Repository<Department> mock;

    @Test
    void getDepartmentByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        var id = new DepartmentIdentifier("very long sor code", DepartmentIdentifier.Type.SOR);
        Department data = new Department(id, Set.of(new Speciality("code", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        Cache<Department, DepartmentIdentifier> departmentCache = new DepartmentCache(new CacheConfiguration(""), mock);
        departmentCache.load();

        var result = departmentCache.get(new DepartmentIdentifier("some missing sor code", DepartmentIdentifier.Type.SOR));

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        var id = new DepartmentIdentifier("very long sor code", DepartmentIdentifier.Type.SOR);
        Department data = new Department(id, Set.of(new Speciality("code", "long clause text")));

        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        Cache<Department, DepartmentIdentifier> departmentCache = new DepartmentCache(new CacheConfiguration(""), mock);
        departmentCache.load();

        var result = departmentCache.get(id);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        var id = new DepartmentIdentifier("very long sor code", DepartmentIdentifier.Type.SOR);
        Department data = new Department(id, Set.of(new Speciality("code", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        Cache<Department, DepartmentIdentifier> departmentCache = new DepartmentCache(new CacheConfiguration(""), mock);
        departmentCache.load();

        Mockito.verify(mock, Mockito.times(1)).findAll();
        var result1 = departmentCache.get(id);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = departmentCache.get(id);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }
}