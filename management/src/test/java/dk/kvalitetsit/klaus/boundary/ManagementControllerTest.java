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

import static dk.kvalitetsit.klaus.MockFactory.clause;
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
        Mockito.when(clauseService.create(Mockito.any(List.class))).then(a -> List.of(clause));
        var result = managementController.v1ClausesPost(List.of(clause));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(List.of(clause), result.getBody());

        var inputArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(clauseService, times(1)).create(inputArgumentCaptor.capture());

        assertNotNull(inputArgumentCaptor.getValue());
        assertEquals(clause, inputArgumentCaptor.getValue().getFirst());
    }

    @Test
    void testPostDSL() {

        Mockito.when(clauseService.createDSL(List.of(dsl))).then(a -> List.of(dsl));
        var result = managementController.v1ClausesDslPost(List.of(dsl));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(List.of(dsl), result.getBody());

        var inputArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(clauseService, times(1)).createDSL(inputArgumentCaptor.capture());

        assertNotNull(inputArgumentCaptor.getValue());
        assertEquals(List.of(dsl), inputArgumentCaptor.getValue());
    }

}
