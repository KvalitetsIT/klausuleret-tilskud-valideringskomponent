package dk.kvalitetsit.itukt.management.boundary;


import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Clause;
import org.openapitools.model.DslInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_DTO;
import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_DSL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ManagementControllerTest {

    @InjectMocks
    private ManagementController managementController;
    @Mock
    private ManagementServiceAdaptor clauseService;

    @Test
    void call20250801clausesPost_CreatesClause() {
        Mockito.when(clauseService.create(Mockito.any(Clause.class))).thenReturn(Optional.of(CLAUSE_1_DTO));

        managementController.call20250801clausesPost(CLAUSE_1_DTO);

        Mockito.verify(clauseService, times(1)).create(CLAUSE_1_DTO);
    }

    @Test
    void call20250801clausesDslPost_CreatesClause() {
        Mockito.when(clauseService.createDSL(Mockito.any(DslInput.class))).thenReturn(Optional.of(CLAUSE_1_DSL));

        DslInput dslInput = new DslInput().dsl(CLAUSE_1_DSL);
        managementController.call20250801clausesDslPost(dslInput);

        Mockito.verify(clauseService, times(1)).createDSL(dslInput);
    }

    @Test
    void call20250801clausesIdGet_WhenClauseExists_ReturnsClause() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.of(CLAUSE_1_DTO));

        var clauseResponse = managementController.call20250801clausesIdGet(uuid);

        assertEquals(CLAUSE_1_DTO, clauseResponse.getBody());
    }

    @Test
    void call20250801clausesIdGet_WhenClauseDoesNotExist_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.empty());

        var e = assertThrows(RuntimeException.class, () -> managementController.call20250801clausesIdGet(uuid));
        assertEquals("Clause was not found", e.getMessage());
    }

    @Test
    void call20250801clausesGet_ReturnsClausesFromService() {
        Mockito.when(clauseService.read_all()).thenReturn(List.of(CLAUSE_1_DTO));

        var clausesResponse = managementController.call20250801clausesGet();

        assertEquals(List.of(CLAUSE_1_DTO), clausesResponse.getBody());
    }
}
