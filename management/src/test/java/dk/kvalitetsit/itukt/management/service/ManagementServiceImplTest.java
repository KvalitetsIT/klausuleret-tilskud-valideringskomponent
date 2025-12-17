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
    void create_WhenClauseNameDoesNotAlreadyExist_CreatesClause() {
        var clauseForCreation = mock(ClauseInput.class);
        var clause = mock(Clause.class);
        Mockito.when(dao.create(clauseForCreation)).thenReturn(clause);
        Mockito.when(dao.nameExists(Mockito.any())).thenReturn(false);

        var result = service.create(clauseForCreation);

        assertEquals(clause, result, "Created clause should be returned from service");
    }

    @Test
    void create_WhenClauseNameAlreadyExists_ThrowsBadRequestException() {
        var clauseForCreation = mock(ClauseInput.class);
        Mockito.when(clauseForCreation.name()).thenReturn("test");
        Mockito.when(dao.nameExists(clauseForCreation.name())).thenReturn(true);

        var e = assertThrows(BadRequestException.class, () -> service.create(clauseForCreation));

        assertEquals("Clause with name 'test' already exists", e.getDetailedError(),
                "Exception message should indicate duplicate clause name");
    }

    @Test
    void update_WhenClauseNameDoesNotExist_ThrowsNotFoundException() {
        var clauseForUpdate = mock(ClauseInput.class);
        Mockito.when(clauseForUpdate.name()).thenReturn("test");
        Mockito.when(dao.nameExists(Mockito.any())).thenReturn(false);

        var e = assertThrows(NotFoundException.class, () -> service.update(clauseForUpdate));

        assertEquals("Clause with name 'test' not found", e.getDetailedError(),
                "Exception message should indicate clause not found");
    }

    @Test
    void update_WhenClauseNameAlreadyExists_CreatesNewClauseVersion() {
        var clauseForUpdate = mock(ClauseInput.class);
        var clause = mock(Clause.class);
        Mockito.when(dao.nameExists(clauseForUpdate.name())).thenReturn(true);
        Mockito.when(dao.create(clauseForUpdate)).thenReturn(clause);

        var result = service.update(clauseForUpdate);

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
    void testReadAll() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.readAll()).thenReturn(List.of(model));
        var result = service.readAll();
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
    void readHistory_assertThrowsNotFoundIfEmpty() {
        String name = "blaah";
        Mockito.when(dao.readHistory(name)).thenReturn(List.of());
        var e = assertThrows(NotFoundException.class, () ->  service.readHistory(name));
        verify(dao, times(1)).readHistory(name);
        assertEquals(e.getDetailedError(), String.format("clause with name '%s' was not found", name));
    }
}
