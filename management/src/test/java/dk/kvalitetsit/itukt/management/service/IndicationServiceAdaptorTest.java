package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Indication;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IndicationServiceAdaptorTest {
    @Mock
    private StamdataCacheService<dk.kvalitetsit.itukt.common.model.Indication> indicationService;

    @Mock
    private Mapper<dk.kvalitetsit.itukt.common.model.Indication, Indication> mapper;

    @InjectMocks
    private IndicationServiceAdaptor adaptor;

    @Test
    void getAll_ReturnsMappedIndications() {
        var indication1 = Mockito.mock(dk.kvalitetsit.itukt.common.model.Indication.class);
        var indication2 = Mockito.mock(dk.kvalitetsit.itukt.common.model.Indication.class);
        var mappedIndication1 = Mockito.mock(Indication.class);
        var mappedIndication2 = Mockito.mock(Indication.class);

        Mockito.when(indicationService.getAll()).thenReturn(Set.of(indication1, indication2));
        Mockito.when(mapper.map(indication1)).thenReturn(mappedIndication1);
        Mockito.when(mapper.map(indication2)).thenReturn(mappedIndication2);

        var result = adaptor.getAll();

        assertEquals(Set.of(mappedIndication1, mappedIndication2), result);
    }

    @Test
    void get_WithExistingIdentifier_ReturnsMappedIndication() {
        String identifier = "ATC123";
        var sourceIndication = Mockito.mock(dk.kvalitetsit.itukt.common.model.Indication.class);
        var mappedIndication = Mockito.mock(Indication.class);

        Mockito.when(indicationService.get(identifier)).thenReturn(Optional.of(sourceIndication));
        Mockito.when(mapper.map(sourceIndication)).thenReturn(mappedIndication);

        var result = adaptor.get(identifier);

        assertEquals(Optional.of(mappedIndication), result);
    }

    @Test
    void get_WithMissingIdentifier_ReturnsEmpty() {
        String identifier = "ATC123";
        Mockito.when(indicationService.get(identifier)).thenReturn(Optional.empty());

        var result = adaptor.get(identifier);

        assertEquals(Optional.empty(), result);
    }
}