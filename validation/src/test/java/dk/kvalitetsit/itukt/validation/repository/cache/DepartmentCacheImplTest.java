package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.Speciality;
import dk.kvalitetsit.itukt.validation.repository.Repository;
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

    @Mock
    private CacheConfiguration configuration;

    @InjectMocks
    private DepartmentCacheImpl cache;

    @Test
    void getDepartmentByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        Department data = new Department(
                null,
                new Department.Identifier.SOR("very long sor code"),
                Set.of(new Speciality("code", "long clause text"))
        );

        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(new Department.Identifier.SOR("some missing sor code"));

        assertFalse(result.isPresent());
    }

    @Test
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        Department data = new Department(
                null,
                new Department.Identifier.SOR("very long sor code"),
                Set.of(new Speciality("code", "long clause text"))
        );

        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(data.sor());

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        var sor = new Department.Identifier.SOR("very long sor code");
        Department data = new Department(null, sor, Set.of(new Speciality("code", "long clause text")));
        Mockito.when(mock.findAll()).thenReturn(List.of(data));
        cache.load();

        Mockito.verify(mock, Mockito.times(1)).findAll();
        var result1 = cache.get(sor);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(mock);

        var result2 = cache.get(sor);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }
}