package dk.kvalitetsit.itukt.management.boundary;


import dk.kvalitetsit.itukt.common.exceptions.AbstractApiException;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.DslInput;
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
    void call20250801clausesPost_CreatesClause() {
        Mockito.when(clauseService.create(Mockito.any(ClauseInput.class))).thenReturn(CLAUSE_1_OUTPUT);

        managementController.call20250801clausesPost(CLAUSE_1_INPUT);

        Mockito.verify(clauseService, times(1)).create(CLAUSE_1_INPUT);
    }

    @Test
    void call20250801clausesDslPost_CreatesClause() {
        Mockito.when(clauseService.createDSL(Mockito.any(DslInput.class))).thenReturn(CLAUSE_1_DSL_OUTPUT);

        DslInput dslInput = CLAUSE_1_DSL_INPUT;
        managementController.call20250801clausesDslPost(dslInput);

        Mockito.verify(clauseService, times(1)).createDSL(dslInput);
    }

    @Test
    void call20250801clausesIdGet_WhenClauseExists_ReturnsClause() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.of(CLAUSE_1_OUTPUT));

        var clauseResponse = managementController.call20250801clausesIdGet(uuid);

        assertEquals(CLAUSE_1_OUTPUT, clauseResponse.getBody());
    }

    @Test
    void call20250801clausesIdGet_WhenClauseDoesNotExist_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.read(uuid)).thenReturn(Optional.empty());

        var e = assertThrows(AbstractApiException.class, () -> managementController.call20250801clausesIdGet(uuid));
        assertEquals("Clause was not found", e.getDetailedError());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    void call20250801clauseDslIdGet_WhenClauseDoesNotExist_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(clauseService.readDsl(uuid)).thenReturn(Optional.empty());

        var e = assertThrows(AbstractApiException.class, () -> managementController.call20250801clausesDslIdGet(uuid));
        assertEquals("Clause was not found", e.getDetailedError());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }



    @Test
    void call20250801clausesGet_ReturnsClausesFromService() {
        Mockito.when(clauseService.readAll()).thenReturn(List.of(CLAUSE_1_OUTPUT));

        var clausesResponse = managementController.call20250801clausesGet();

        assertEquals(List.of(CLAUSE_1_OUTPUT), clausesResponse.getBody());
    }
}
