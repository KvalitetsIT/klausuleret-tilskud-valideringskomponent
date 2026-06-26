package dk.kvalitetsit.itukt.management.boundary;


import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.management.MockFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ManagementControllerTest {

    @InjectMocks
    private ManagementController managementController;
    @Mock
    private ManagementServiceAdaptor clauseService;

    @BeforeEach
    void setupRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void management20250801ClausesPost_CreatesClause() {
        Mockito.when(clauseService.create(Mockito.any(ClauseInput.class))).thenReturn(CLAUSE_1_OUTPUT);

        managementController.management20250801ClausesPost(CLAUSE_1_INPUT);

        Mockito.verify(clauseService, times(1)).create(CLAUSE_1_INPUT);
    }

    @Test
    void management20250801ClausesDslPost_CreatesClause() {
        Mockito.when(clauseService.createDSL(Mockito.any(DslInput.class))).thenReturn(CLAUSE_1_DSL_OUTPUT);

        DslInput dslInput = CLAUSE_1_DSL_INPUT;
        managementController.management20250801ClausesDslPost(dslInput);

        Mockito.verify(clauseService, times(1)).createDSL(dslInput);
    }

    @Test
    void management20250801ClausesIdGet_WhenClauseExists_ReturnsClause() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.of(CLAUSE_1_OUTPUT));

        var clauseResponse = managementController.management20250801ClausesIdGet(uuid);

        assertEquals(CLAUSE_1_OUTPUT, clauseResponse.getBody());
    }

    @Test
    void management20250801ClausesIdGet_WhenClauseDoesNotExist_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.empty());

        var e = assertThrows(NotFoundException.class, () -> managementController.management20250801ClausesIdGet(uuid));
        assertEquals("Clause was not found", e.getDetailedError());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    void management20250801ClauseDslIdGet_WhenClauseDoesNotExist_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.readDsl(uuid)).thenReturn(Optional.empty());

        var e = assertThrows(NotFoundException.class, () -> managementController.management20250801ClausesDslIdGet(uuid));
        assertEquals("Clause was not found", e.getDetailedError());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    void management20250801ClausesGet_ReturnsClausesFromService() {
        Mockito.when(clauseService.readByStatus(ClauseStatus.DRAFT)).thenReturn(List.of(CLAUSE_1_OUTPUT));

        var clausesResponse = managementController.management20250801ClausesGet(ClauseStatus.DRAFT);

        assertEquals(List.of(CLAUSE_1_OUTPUT), clausesResponse.getBody());
    }

    @Test
    void management20250801ClausesDslGet_ReturnsClausesFromService() {
        Mockito.when(clauseService.readDslByStatus(ClauseStatus.ACTIVE)).thenReturn(List.of(CLAUSE_1_DSL_OUTPUT));

        var clausesResponse = managementController.management20250801ClausesDslGet(ClauseStatus.ACTIVE);

        assertEquals(List.of(CLAUSE_1_DSL_OUTPUT), clausesResponse.getBody());
    }

    @Test
    void management20250801ClausesDraftsIdStatusPut_UpdatesClauseStatus() {
        UUID uuid = UUID.randomUUID();
        var status = new DraftClauseStatusInput(false, DraftClauseStatusInput.StatusEnum.ACTIVE);

        managementController.management20250801ClausesDraftsIdStatusPut(uuid, status);

        Mockito.verify(clauseService, times(1)).approveClause(uuid, false);
    }

    @Test
    void management20250801ClausesNameStatusPut_GivenAnInactiveStatus_UpdatesClauseStatus() {
        String name = "test";
        var status = new ClauseStatusInput(ClauseStatusInput.StatusEnum.INACTIVE);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(clauseService.inactivateClause(name)).thenReturn(dslOutput);

        var response = managementController.management20250801ClausesNameStatusPut(name, status);

        assertEquals(dslOutput, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void call20250801clausesNameStatusPut_GivenActiveStatus_UpdatesClauseStatus() {
        String name = "test";
        var status = new ClauseStatusInput(ClauseStatusInput.StatusEnum.ACTIVE);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(clauseService.activateClause(name)).thenReturn(dslOutput);

        var response = managementController.management20250801ClausesNameStatusPut(name, status);

        assertEquals(dslOutput, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void management20250801ClausesIdDelete() {
        UUID uuid = UUID.randomUUID();
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(clauseService.deleteDraft(uuid)).thenReturn(clauseOutput);
        var response = managementController.management20250801ClausesIdDelete(uuid);
        assertEquals(clauseOutput, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void management20250801ClausesIdDelete_whenError() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.deleteDraft(uuid)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> managementController.management20250801ClausesIdDelete(uuid));
    }

    @Test
    void management20250801ClausesNameDrugCountGet_ReturnsDrugCount() {
        String name = "test";
        var drugCount = new DrugCount().drugCount(5L);
        Mockito.when(clauseService.getNumberOfDrugsForClause(name)).thenReturn(drugCount);

        var response = managementController.management20250801ClausesNameDrugCountGet(name);

        assertEquals(drugCount, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
