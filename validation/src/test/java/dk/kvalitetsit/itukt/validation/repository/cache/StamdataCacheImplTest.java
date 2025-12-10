package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
import org.junit.jupiter.api.Disabled;
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
class StamdataCacheImplTest {

    @Mock
    private StamDataRepositoryAdaptor adaptor;

    @Mock
    private CacheConfiguration configuration;

    @InjectMocks
    private StamdataCacheImpl cache;


    @Test
    @Disabled
    void getStamDataByDrugId_WhenDrugIdIsNotInCache_ReturnsEmptyOptional() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("code", "long clause text")));
        Mockito.when(adaptor.findAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(2L);

        assertFalse(result.isPresent());
    }

    @Test
    @Disabled
    void get_WhenDrugIdIsInCache_ReturnsStamDataName() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("code", "long clause text")));

        Mockito.when(adaptor.findAll()).thenReturn(List.of(data));
        cache.load();

        var result = cache.get(drugId);

        assertTrue(result.isPresent());
        assertEquals(data, result.get());
    }

    @Test
    @Disabled
    void getClauseByDrugId_WhenDrugIdIsInCache_ReturnsStamdataAndNoMoreInteractions() {
        long drugId = 1L;
        StamData data = new StamData(new StamData.Drug(drugId), Set.of(new StamData.Clause("code", "long clause text")));
        Mockito.when(adaptor.findAll()).thenReturn(List.of(data));
        cache.load();

        Mockito.verify(adaptor, Mockito.times(1)).findAll();
        var result1 = cache.get(drugId);
        assertTrue(result1.isPresent(), "Expected a result since the cache has been reload already");
        assertEquals(data, result1.get(), "Expected the cache to return the same as was loaded from the concrete repository");
        Mockito.verifyNoMoreInteractions(adaptor);

        var result2 = cache.get(drugId);
        assertEquals(result1, result2, "Expected the data to be the same as previously returned by the first invocation");

    }
}