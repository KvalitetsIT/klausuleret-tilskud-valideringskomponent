package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
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
class MedicationRouteCacheTest {
    @Mock
    private CacheConfiguration configuration;

    @Mock
    private Repository<Medication.Route> repository;

    @InjectMocks
    private MedicationRouteCache routeCache;

    @Test
    void getAll_BeforeRun_ReturnsEmptySet() {
        assertTrue(routeCache.getAll().isEmpty());
    }

    @Test
    void getAll_AfterRun_ReturnsDistinctRoutes() {
        var route1 = new Medication.Route("routeA");
        var route2 = new Medication.Route("routeB");
        var route3 = new Medication.Route("routeB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(route1, route2, route3));

        routeCache.run();
        var routes = routeCache.getAll();

        var expected = Set.of(route1, route2);
        assertEquals(expected, routes);
    }

    @Test
    void get_NotMatchingRouteFromLoad_ReturnsEmpty() {
        var route1 = new Medication.Route("routeA");
        var route2 = new Medication.Route("routeB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(route1, route2));

        routeCache.run();
        var result = routeCache.get("nonExistingRouteCode");

        assertTrue(result.isEmpty());
    }

    @Test
    void get_MatchingRouteFromLoad_ReturnsRoute() {
        var route1 = new Medication.Route("routeA");
        var route2 = new Medication.Route("routeB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(route1, route2));

        routeCache.run();
        var result = routeCache.get(route1.code());

        assertTrue(result.isPresent());
        assertEquals(route1, result.get());
    }

    @Test
    void get_BeforeRun_ReturnsEmpty() {
        assertTrue(routeCache.get("someRouteCode").isEmpty());
    }
}