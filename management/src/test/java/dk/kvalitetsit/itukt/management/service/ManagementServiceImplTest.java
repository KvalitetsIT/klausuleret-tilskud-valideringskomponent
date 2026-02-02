package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dk.kvalitetsit.itukt.management.MockFactory.EXPRESSION_1_MODEL;
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
    void create_CreatesDraftClause() {
        var clauseForCreation = new ClauseInput("test", Mockito.mock(BinaryExpression.class), "test error");
        var clause = mock(Clause.class);
        Mockito.when(dao.create(clauseForCreation.name(), clauseForCreation.expression(), clauseForCreation.errorMessage(), Clause.Status.DRAFT, null))
                .thenReturn(clause);

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
    void readByStatus_WithStatusActive_ReturnsLatestActiveClauses() {
        var activeClause = mock(Clause.class);
        Mockito.when(activeClause.status()).thenReturn(Clause.Status.ACTIVE);
        var inactiveClause = mock(Clause.class);
        Mockito.when(inactiveClause.status()).thenReturn(Clause.Status.INACTIVE);
        Mockito.when(dao.readLatestVersions()).thenReturn(List.of(activeClause, inactiveClause));

        var result = service.readByStatus(Clause.Status.ACTIVE);

        assertEquals(List.of(activeClause), result);
    }

    @Test
    void readByStatus_WithStatusInactive_ReturnsLatestInactiveClauses() {
        var activeClause = mock(Clause.class);
        Mockito.when(activeClause.status()).thenReturn(Clause.Status.ACTIVE);
        var inactiveClause = mock(Clause.class);
        Mockito.when(inactiveClause.status()).thenReturn(Clause.Status.INACTIVE);
        Mockito.when(dao.readLatestVersions()).thenReturn(List.of(activeClause, inactiveClause));

        var result = service.readByStatus(Clause.Status.INACTIVE);

        assertEquals(List.of(inactiveClause), result);
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
                new Clause(1L, name, Clause.Status.ACTIVE, null, new Clause.Error("message1", 10800), null, Optional.empty()),
                new Clause(2L, name, Clause.Status.INACTIVE, null, new Clause.Error("message2", 10800), null, Optional.empty())
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

    @Test
    void inactivate_WhenClauseDoesNotExist_ThrowsException() {
        Mockito.when(dao.read(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.inactivate(UUID.randomUUID()));
    }

    @Test
    void inactivate_WhenClauseIsInDraft_ThrowsException() {
        UUID clauseUuid = UUID.randomUUID();
        var clause = mock(Clause.class);
        Mockito.when(clause.status()).thenReturn(Clause.Status.DRAFT);
        Mockito.when(dao.read(Mockito.any())).thenReturn(Optional.of(clause));

        assertThrows(BadRequestException.class, () -> service.inactivate(clauseUuid));
    }

    @Test
    void inactivate_WhenClauseIsAlreadyInactive_ThrowsException() {
        UUID clauseUuid = UUID.randomUUID();
        var clause = mock(Clause.class);
        Mockito.when(clause.status()).thenReturn(Clause.Status.INACTIVE);
        Mockito.when(dao.read(Mockito.any())).thenReturn(Optional.of(clause));

        assertThrows(BadRequestException.class, () -> service.inactivate(clauseUuid));
    }

    @Test
    void inactivate_WhenClauseIsActive_CreatesNewClauseAndSetsInactive() {
        var clause = new Clause(1L, "test", Clause.Status.ACTIVE, UUID.randomUUID(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL, Optional.of(new Date()));
        Mockito.when(dao.read(Mockito.any())).thenReturn(Optional.of(clause));

        service.inactivate(clause.uuid());

        Mockito.verify(dao).create(Mockito.eq(clause.name()), Mockito.eq(clause.expression()), Mockito.eq(clause.error().message()), Mockito.eq(Clause.Status.INACTIVE), Mockito.any(Date.class));
    }
}
