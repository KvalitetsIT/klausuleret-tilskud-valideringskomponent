package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.service.ManagementServiceAdaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Clause;
import org.openapitools.model.DslInput;

import java.util.Optional;

import static dk.kvalitetsit.klaus.MockFactory.clauseDto;
import static dk.kvalitetsit.klaus.MockFactory.dsl;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ManagementControllerTest {


    @InjectMocks
    private ManagementController managementController;
    @Mock
    private ManagementServiceAdaptor clauseService;

    @Test
    void testPostClause() {
        Mockito.when(clauseService.create(Mockito.any(Clause.class))).thenReturn(Optional.of(clauseDto));

        managementController.call20250801clausesPost(clauseDto);

        Mockito.verify(clauseService, times(1)).create(clauseDto);
    }

    @Test
    void testPostDSL() {
        Mockito.when(clauseService.createDSL(Mockito.any(DslInput.class))).thenReturn(Optional.of(dsl));

        DslInput dslInput = new DslInput().dsl(dsl);
        managementController.call20250801clausesDslPost(dslInput);

        Mockito.verify(clauseService, times(1)).createDSL(dslInput);
    }

}
