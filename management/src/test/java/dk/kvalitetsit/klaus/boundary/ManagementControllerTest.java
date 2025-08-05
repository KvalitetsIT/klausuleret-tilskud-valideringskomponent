package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.service.ManagementServiceAdaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dk.kvalitetsit.klaus.MockFactory.clauseDto;
import static dk.kvalitetsit.klaus.MockFactory.dsl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ManagementControllerTest {


    @InjectMocks
    private ManagementController managementController;
    @Mock
    private ManagementServiceAdaptor clauseService;

    @Test
    void testPostNonDSL() {
        Mockito.when(clauseService.create(Mockito.any(List.class))).then(a -> List.of(clauseDto));
        var result = managementController.call20250801clausesPost(List.of(clauseDto));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(List.of(clauseDto), result.getBody());

        var inputArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(clauseService, times(1)).create(inputArgumentCaptor.capture());

        assertNotNull(inputArgumentCaptor.getValue());
        assertEquals(clauseDto, inputArgumentCaptor.getValue().getFirst());
    }

    @Test
    void testPostDSL() {

        Mockito.when(clauseService.createDSL(List.of(dsl))).then(a -> List.of(dsl));
        var result = managementController.call20250801clausesDslPost(List.of(dsl));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(List.of(dsl), result.getBody());

        var inputArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(clauseService, times(1)).createDSL(inputArgumentCaptor.capture());

        assertNotNull(inputArgumentCaptor.getValue());
        assertEquals(List.of(dsl), inputArgumentCaptor.getValue());
    }

}
