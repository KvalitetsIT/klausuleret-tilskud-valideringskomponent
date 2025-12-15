package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.CreatorProperty;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {
    @InjectMocks
    private ManagementServiceImpl service;
    @Mock
    private ClauseRepositoryAdaptor dao;

    @Test
    void create_CreatesClause() {
        var clauseForCreation = mock(ClauseInput.class);
        var clause = mock(Clause.class);
        Mockito.when(dao.create(clauseForCreation)).thenReturn(clause);

        var result = service.create(clauseForCreation);

        assertEquals(clause, result, "Created clause should be returned from service");
    }

    @Test
    void testRead() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.read(model.uuid())).thenReturn(Optional.of(model));
        var result = service.read(model.uuid());
        assertEquals(Optional.of(model), result);
    }

    @Test
    void readByStatus_WithStatusActive_ReturnsActiveClauses() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.readAllActive()).thenReturn(List.of(model));

        var result = service.readByStatus(Clause.Status.ACTIVE);

        assertEquals(List.of(model), result);
    }

    @Test
    void readByStatus_WithStatusDraft_ReturnsDraftClauses() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.readAllDrafts()).thenReturn(List.of(model));

        var result = service.readByStatus(Clause.Status.DRAFT);

        assertEquals(List.of(model), result);
    }

    @Test
    void readHistory_invokesRepositoryOnce() {
        String name = "blaah";
        var clauses = List.of(
                new Clause(1L, name, null, new Clause.Error("message1", 10800), null, null),
                new Clause(2L, name, null, new Clause.Error("message2", 10800), null, null)
        );
        Mockito.when(dao.readHistory(name)).thenReturn(clauses);
        var result = service.readHistory(name);
        verify(dao, times(1)).readHistory(name);
        assertEquals(clauses, result);
    }

    @Test
    void approve_UpdatesClauseStatusFromDraftToActive() {
        var uuid = UUID.randomUUID();

        service.approve(uuid);

        Mockito.verify(dao, Mockito.times(1)).updateDraftToActive(uuid);
    }

    @Test
    void readHistory_assertThrowsNotFoundIfEmpty() {
        String name = "blaah";
        Mockito.when(dao.readHistory(name)).thenReturn(List.of());
        var e = assertThrows(NotFoundException.class, () ->  service.readHistory(name));
        verify(dao, times(1)).readHistory(name);
        assertEquals(e.getDetailedError(), String.format("clause with name '%s' was not found", name));
    }
}
