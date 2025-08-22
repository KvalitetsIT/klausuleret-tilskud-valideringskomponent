package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.validation.boundary.mapping.ValidationDataContextMapper;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ValidationRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceAdaptorTest {
    @InjectMocks
    private ValidationServiceAdaptor adaptor;

    @Mock
    private ValidationServiceImpl concreteService;

    @BeforeEach
    void setUp() {
        adaptor = new ValidationServiceAdaptor(concreteService, new ValidationDataContextMapper());
    }

    @Test
    void testValidate() {

        Mockito.when(concreteService.validate(Mockito.any(DataContext.class))).thenReturn(true);

        var request = new ValidationRequest().age(20);
        var result = adaptor.validate(request);
        assertTrue(result);

        var captor = ArgumentCaptor.forClass(DataContext.class);
        Mockito.verify(concreteService, Mockito.times(1)).validate(captor.capture());

        var ctx = new DataContext(Map.of("ALDER", List.of("20")));
        assertEquals(ctx, captor.getValue());
    }


}
