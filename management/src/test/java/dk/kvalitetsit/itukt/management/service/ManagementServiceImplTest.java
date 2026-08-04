package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;
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
    @Mock
    private SkippedValidationRepository skippedValidationRepository;
    @Mock
    private UserContextService userContextService;

    @Test
    void create_WhenDraftWithSameNameAlreadyExists_ThrowsException() {
        var clauseForCreation = new ClauseInput("test", Mockito.mock(BinaryExpression.class), "test error");
        var existingDraftClause = mock(Clause.class);
        Mockito.when(dao.readCurrentDraft(clauseForCreation.name())).thenReturn(Optional.of(existingDraftClause));

        var e = assertThrows(InvalidInputException.class, () -> service.create(clauseForCreation));
        assertEquals(e.getMessage(), String.format("A draft clause with name '%s' already exists", clauseForCreation.name()));
    }

    @Test
    void create_WhenNoClauseWithMatchingNameExists_CreatesDraftClauseWithoutParent() throws InvalidInputException {
        var clauseForCreation = new ClauseInput("test", Mockito.mock(BinaryExpression.class), "test error");
        String userId = "tester";
        Mockito.when(userContextService.getUserID()).thenReturn(userId);
        var expectedClauseFullInput = new ClauseFullInput(clauseForCreation.name(), clauseForCreation.expression(), clauseForCreation.errorMessage(), Clause.Status.DRAFT, userId, null);
        var clause = mock(Clause.class);
        Mockito.when(dao.create(expectedClauseFullInput))
                .thenReturn(clause);

        var result = service.create(clauseForCreation);

        assertEquals(clause, result, "Created clause should be returned from service");
    }

    @Test
    void create_WhenNonDraftClauseWithMatchingNameExists_CreatesDraftClauseWithParent() throws InvalidInputException {
        var clauseForCreation = new ClauseInput("test", Mockito.mock(BinaryExpression.class), "test error");
        String userId = "tester";
        Mockito.when(userContextService.getUserID()).thenReturn(userId);
        var existingClause = mock(Clause.class);
        Mockito.when(existingClause.id()).thenReturn(1L);
        Mockito.when(dao.readCurrentNonDraftClause(clauseForCreation.name())).thenReturn(Optional.of(existingClause));
        var expectedClauseFullInput = new ClauseFullInput(clauseForCreation.name(), clauseForCreation.expression(), clauseForCreation.errorMessage(), Clause.Status.DRAFT, userId, existingClause.id());
        var clause = mock(Clause.class);
        Mockito.when(dao.create(expectedClauseFullInput))
                .thenReturn(clause);

        var result = service.create(clauseForCreation);

        assertEquals(clause, result, "Created clause should be returned from service");
    }

    @Test
    void update_WhenNameDoesNotMatchADraftClause_ThrowsException() {
        var name = "test";
        var clauseForUpdate = new ClauseUpdateInput(Mockito.mock(BinaryExpression.class), "test error");
        Mockito.when(dao.readCurrentDraft(name)).thenReturn(Optional.empty());

        var e = assertThrows(NotFoundException.class, () -> service.updateDraft(name, clauseForUpdate));
        assertEquals("No current draft found with name '%s'".formatted(name), e.getMessage());
    }

    @Test
    void update_WhenNameMatchesADraftClause_CreatesDraftClauseWithParent() throws ManagementException {
        var name = "test";
        var clauseForUpdate = new ClauseUpdateInput(Mockito.mock(BinaryExpression.class), "test error");
        String userId = "tester";
        Mockito.when(userContextService.getUserID()).thenReturn(userId);
        var existingDraft = mock(Clause.class);
        Mockito.when(existingDraft.id()).thenReturn(1L);
        Mockito.when(dao.readCurrentDraft(name)).thenReturn(Optional.of(existingDraft));
        var expectedDraftOutput = mock(Clause.class);
        Mockito.when(dao.create(Mockito.any())).thenReturn(expectedDraftOutput);

        var draftOutput = service.updateDraft(name, clauseForUpdate);

        assertEquals(expectedDraftOutput, draftOutput, "Updated draft clause should be returned from service");
        var expectedClauseInput = new ClauseFullInput(name, clauseForUpdate.expression(), clauseForUpdate.errorMessage(), Clause.Status.DRAFT, userId, existingDraft.id());
        Mockito.verify(dao, times(1)).create(expectedClauseInput);
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
        Mockito.when(dao.readCurrentNonDraftClauses()).thenReturn(List.of(activeClause, inactiveClause));

        var result = service.readByStatus(Clause.Status.ACTIVE);

        assertEquals(List.of(activeClause), result);
    }

    @Test
    void readByStatus_WithStatusInactive_ReturnsLatestInactiveClauses() {
        var activeClause = mock(Clause.class);
        Mockito.when(activeClause.status()).thenReturn(Clause.Status.ACTIVE);
        var inactiveClause = mock(Clause.class);
        Mockito.when(inactiveClause.status()).thenReturn(Clause.Status.INACTIVE);
        Mockito.when(dao.readCurrentNonDraftClauses()).thenReturn(List.of(activeClause, inactiveClause));

        var result = service.readByStatus(Clause.Status.INACTIVE);

        assertEquals(List.of(inactiveClause), result);
    }

    @Test
    void readByStatus_WithStatusDraft_ReturnsDraftClauses() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.readCurrentDrafts()).thenReturn(List.of(model));

        var result = service.readByStatus(Clause.Status.DRAFT);

        assertEquals(List.of(model), result);
    }

    @Test
    void readHistory_invokesRepositoryOnce() throws NotFoundException {
        String name = "blaah";
        var clauses = List.of(
                new Clause(1L, name, Clause.Status.ACTIVE, null, new Clause.Error("message1", 10800), null, "tester", new Date()),
                new Clause(2L, name, Clause.Status.INACTIVE, null, new Clause.Error("message2", 10800), null, "tester", new Date())
        );
        Mockito.when(dao.readHistory(name)).thenReturn(clauses);
        var result = service.readHistory(name);
        verify(dao, times(1)).readHistory(name);
        assertEquals(clauses, result);
    }

    @Test
    void approve_givenNoResetSkippedValidations_whenApprove_thenUpdateClauseStatusFromDraftToActiveAndCopySkippedValidations() throws ManagementException {
        var active = Mockito.mock(Clause.class);
        var draft = Mockito.mock(Clause.class);

        Mockito.when(active.id()).thenReturn(1L);
        Mockito.when(draft.name()).thenReturn("blaaah");
        Mockito.when(draft.expression()).thenReturn(Mockito.mock(BinaryExpression.class));
        Mockito.when(draft.error()).thenReturn(new Clause.Error("message", 10800));
        Mockito.when(draft.uuid()).thenReturn(UUID.randomUUID());
        String userId = "tester";
        Mockito.when(userContextService.getUserID()).thenReturn(userId);
        Mockito.when(dao.readCurrentDrafts()).thenReturn(List.of(draft));
        Mockito.when(dao.readCurrentNonDraftClause(draft.name())).thenReturn(Optional.of(active));
        var createdClause = mock(Clause.class);
        Mockito.when(dao.create(Mockito.any())).thenReturn(createdClause);
        Mockito.when(createdClause.id()).thenReturn(3L);

        service.approve(draft.uuid(), false);

        Mockito.verify(skippedValidationRepository, Mockito.times(1)).copySkippedValidation(active.id(), createdClause.id());
        var expectedClauseInput = new ClauseFullInput(draft.name(), draft.expression(), draft.error().message(), Clause.Status.ACTIVE, userId, draft.id());
        Mockito.verify(dao, Mockito.times(1)).create(expectedClauseInput);
    }

    @Test
    void approve_givenResetSkippedValidations_whenApprove_thenUpdateClauseStatusFromDraftToActiveWithoutCopyingSkippedValidations() throws ManagementException {
        var active = Mockito.mock(Clause.class);
        var draft = Mockito.mock(Clause.class);
        Mockito.when(draft.name()).thenReturn("blaaah");
        Mockito.when(draft.expression()).thenReturn(Mockito.mock(BinaryExpression.class));
        Mockito.when(draft.error()).thenReturn(Mockito.mock(Clause.Error.class));
        Mockito.when(draft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(dao.readCurrentDrafts()).thenReturn(List.of(draft));
        Mockito.when(dao.readCurrentNonDraftClause(draft.name())).thenReturn(Optional.of(active));

        service.approve(draft.uuid(), true);

        Mockito.verifyNoInteractions(skippedValidationRepository);
        Mockito.verify(dao, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void approve_WhenNoDraftMatchesUuid_ThrowsException() {
        var uuid = UUID.randomUUID();
        var anotherDraft = mock(Clause.class);
        Mockito.when(anotherDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(dao.readCurrentDrafts()).thenReturn(List.of(anotherDraft));

        var e = assertThrows(NotFoundException.class, () -> service.approve(uuid, true));

        assertEquals(e.getMessage(), "Clause %s is not a current draft and can not be approved".formatted(uuid));
        Mockito.verifyNoInteractions(skippedValidationRepository);
        Mockito.verifyNoMoreInteractions(dao);
    }

    @Test
    void approve_givenNoResetSkippedValidationsWithNoCurrentClause_UpdatesClauseStatusFromDraftToActiveWithoutCopyingSkippedValidations() throws ManagementException {
        var draft = Mockito.mock(Clause.class);
        Mockito.when(draft.name()).thenReturn("blaaah");
        Mockito.when(draft.expression()).thenReturn(Mockito.mock(BinaryExpression.class));
        Mockito.when(draft.error()).thenReturn(Mockito.mock(Clause.Error.class));
        Mockito.when(draft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(dao.readCurrentDrafts()).thenReturn(List.of(draft));
        Mockito.when(dao.readCurrentNonDraftClause(draft.name())).thenReturn(Optional.empty());

        service.approve(draft.uuid(), false);

        Mockito.verifyNoInteractions(skippedValidationRepository);
        Mockito.verify(dao, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void readHistory_assertThrowsNotFoundIfEmpty() {
        String name = "blaah";
        Mockito.when(dao.readHistory(name)).thenReturn(List.of());
        var e = assertThrows(NotFoundException.class, () -> service.readHistory(name));
        verify(dao, times(1)).readHistory(name);
        assertEquals(e.getMessage(), String.format("clause with name '%s' was not found", name));
    }

    @Test
    void readDraftHistory_WhenNoDraftMatchesName_ThrowsException() {
        String name = "test";
        Mockito.when(dao.readCurrentDraft(name)).thenReturn(Optional.empty());

        var e = assertThrows(NotFoundException.class, () -> service.readDraftHistory(name));

        assertEquals(e.getMessage(), String.format("Clause draft with name '%s' was not found", name));
    }

    @Test
    void readDraftHistory_WhenDraftHasNoParent_ReturnsOnlyCurrentDraft() throws NotFoundException {
        String name = "test";
        var currentDraft = mock(Clause.class);
        Mockito.when(currentDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(currentDraft.status()).thenReturn(Clause.Status.DRAFT);
        Mockito.when(dao.readCurrentDraft(name)).thenReturn(Optional.of(currentDraft));
        Mockito.when(dao.readParent(currentDraft.uuid())).thenReturn(Optional.empty());

        var result = service.readDraftHistory(name);

        assertEquals(List.of(currentDraft), result);
    }

    @Test
    void readDraftHistory_WhenDraftHasDraftParentWithActiveParent_ReturnsOnlyChildrenOfActiveClause() throws NotFoundException {
        String name = "test";
        var currentDraft = mock(Clause.class);
        var parentDraft = mock(Clause.class);
        var activeParent = mock(Clause.class);
        Mockito.when(currentDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(currentDraft.status()).thenReturn(Clause.Status.DRAFT);
        Mockito.when(parentDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(parentDraft.status()).thenReturn(Clause.Status.DRAFT);
        Mockito.when(activeParent.status()).thenReturn(Clause.Status.ACTIVE);
        Mockito.when(dao.readCurrentDraft(name)).thenReturn(Optional.of(currentDraft));
        Mockito.when(dao.readParent(currentDraft.uuid())).thenReturn(Optional.of(parentDraft));
        Mockito.when(dao.readParent(parentDraft.uuid())).thenReturn(Optional.of(activeParent));

        var result = service.readDraftHistory(name);

        assertEquals(List.of(currentDraft, parentDraft), result);
        Mockito.verify(dao, times(1)).readCurrentDraft(name);
        Mockito.verify(dao, times(1)).readParent(currentDraft.uuid());
        Mockito.verify(dao, times(1)).readParent(parentDraft.uuid());
        Mockito.verifyNoMoreInteractions(dao);
    }

    @Test
    void inactivate_WhenClauseDoesNotExist_ThrowsException() {
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InvalidInputException.class, () -> service.inactivate("test"));
    }

    @Test
    void inactivate_WhenClauseIsAlreadyInactive_ThrowsException() {
        var clause = mock(Clause.class);
        Mockito.when(clause.status()).thenReturn(Clause.Status.INACTIVE);
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.of(clause));

        assertThrows(InvalidInputException.class, () -> service.inactivate("test"));
    }

    @Test
    void givenAnActiveClause_whenInactivate_thenEnsureSkippedValidationIsCopied() throws InvalidInputException {
        var clause = new Clause(1L, "test", Clause.Status.ACTIVE, UUID.randomUUID(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL, "tester", new Date());
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.of(clause));

        Clause created = new Clause(2L, clause.name(), clause.status(), UUID.randomUUID(), clause.error(), clause.expression(), "tester", new Date());
        Mockito.when(dao.create(any())).thenReturn(created);

        service.inactivate("test");

        Mockito.verify(skippedValidationRepository, Mockito.times(1)).copySkippedValidation(clause.id(), created.id());
    }

    @Test
    void givenAnInactiveClause_whenActivate_thenEnsureSkippedValidationIsCopied() throws InvalidInputException {
        var clause = new Clause(1L, "test", Clause.Status.INACTIVE, UUID.randomUUID(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL, "tester", new Date());
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.of(clause));

        Clause created = new Clause(2L, clause.name(), clause.status(), UUID.randomUUID(), clause.error(), clause.expression(), "tester", new Date());
        Mockito.when(dao.create(any())).thenReturn(created);

        service.activate("test");

        Mockito.verify(skippedValidationRepository, Mockito.times(1)).copySkippedValidation(clause.id(), created.id());
    }

    @Test
    void inactivate_WhenClauseIsActive_CreatesNewClauseAndSetsInactive() throws InvalidInputException {
        var clause = new Clause(1L, "test", Clause.Status.ACTIVE, UUID.randomUUID(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL, "tester", new Date());
        Mockito.when(dao.readCurrentNonDraftClause(clause.name())).thenReturn(Optional.of(clause));
        var inactiveClause = Mockito.mock(Clause.class);
        Mockito.when(dao.create(Mockito.any())).thenReturn(inactiveClause);

        var clauseResponse = service.inactivate(clause.name());

        assertEquals(inactiveClause, clauseResponse);
        var expectedClauseInput = new ClauseFullInput(clause.name(), clause.expression(), clause.error().message(), Clause.Status.INACTIVE, clause.createdBy(), clause.id());
        Mockito.verify(dao, Mockito.times(1)).create(expectedClauseInput);
    }

    @Test
    void activate_WhenClauseDoesNotExist_ThrowsException() {
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InvalidInputException.class, () -> service.activate("test"));
    }

    @Test
    void activate_WhenClauseIsAlreadyActive_ThrowsException() {
        var clause = mock(Clause.class);
        Mockito.when(clause.status()).thenReturn(Clause.Status.ACTIVE);
        Mockito.when(dao.readCurrentNonDraftClause(Mockito.any())).thenReturn(Optional.of(clause));

        assertThrows(InvalidInputException.class, () -> service.activate("test"));
    }

    @Test
    void activate_WhenClauseIsInactive_CreatesNewClauseAndSetsActive() throws InvalidInputException {
        var clause = new Clause(1L, "test", Clause.Status.INACTIVE, UUID.randomUUID(), new Clause.Error("message", 10800), EXPRESSION_1_MODEL, "tester", new Date());
        Mockito.when(dao.readCurrentNonDraftClause(clause.name())).thenReturn(Optional.of(clause));
        var activeClause = Mockito.mock(Clause.class);
        Mockito.when(dao.create(Mockito.any())).thenReturn(activeClause);

        var clauseResponse = service.activate(clause.name());

        assertEquals(activeClause, clauseResponse);
        var expectedClauseInput = new ClauseFullInput(clause.name(), clause.expression(), clause.error().message(), Clause.Status.ACTIVE, clause.createdBy(), clause.id());
        Mockito.verify(dao, Mockito.times(1)).create(expectedClauseInput);
    }

    @Test
    void deleteDraft_WhenNoClauseExistsForUuid_ThrowsException() {
        UUID uuid = UUID.randomUUID();

        var e = assertThrows(NotFoundException.class, () -> service.deleteDraft(uuid));

        assertEquals("Clause %s is not a current draft and can not be deleted".formatted(uuid), e.getMessage());
    }

    @Test
    void deleteDraft_WhenNoCurrentDraftIsFoundForClause_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        var clause = mock(Clause.class);
        Mockito.when(clause.name()).thenReturn("test");
        Mockito.when(dao.read(uuid)).thenReturn(Optional.of(clause));
        Mockito.when(dao.readCurrentDraft(clause.name())).thenReturn(Optional.empty());

        var e = assertThrows(NotFoundException.class, () -> service.deleteDraft(uuid));

        assertEquals("Clause %s is not a current draft and can not be deleted".formatted(uuid), e.getMessage());
    }

    @Test
    void deleteDraft_WhenUuidDoesNotMatchTheCurrentDraft_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        var clause = mock(Clause.class);
        Mockito.when(clause.name()).thenReturn("test");
        Mockito.when(dao.read(uuid)).thenReturn(Optional.of(clause));
        var currentDraft = mock(Clause.class);
        Mockito.when(currentDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(dao.readCurrentDraft(clause.name())).thenReturn(Optional.of(currentDraft));

        var e = assertThrows(NotFoundException.class, () -> service.deleteDraft(uuid));

        assertEquals("Clause %s is not a current draft and can not be deleted".formatted(uuid), e.getMessage());
    }

    @Test
    void deleteDraft_WithTwoDraftVersions_DeletesBothVersions() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        var latestDraft = mock(Clause.class);
        Mockito.when(latestDraft.name()).thenReturn("test");
        Mockito.when(latestDraft.uuid()).thenReturn(uuid);
        var secondLatestDraft = mock(Clause.class);
        Mockito.when(secondLatestDraft.uuid()).thenReturn(UUID.randomUUID());
        Mockito.when(dao.read(uuid)).thenReturn(Optional.of(latestDraft));
        Mockito.when(dao.readCurrentDraft(latestDraft.name()))
                .thenReturn(Optional.of(latestDraft))
                .thenReturn(Optional.of(latestDraft))
                .thenReturn(Optional.of(secondLatestDraft))
                .thenReturn(Optional.empty());
        Mockito.when(dao.deleteDraft(latestDraft.uuid())).thenReturn(latestDraft);
        Mockito.when(dao.deleteDraft(secondLatestDraft.uuid())).thenReturn(secondLatestDraft);

        service.deleteDraft(uuid);

        Mockito.verify(dao, times(1)).deleteDraft(latestDraft.uuid());
        Mockito.verify(dao, times(1)).deleteDraft(secondLatestDraft.uuid());
    }
}
