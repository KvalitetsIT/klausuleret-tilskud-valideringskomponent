package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {
    @InjectMocks
    private ManagementServiceImpl service;
    @Mock
    private ClauseRepositoryAdaptor dao;

    @Test
    void create_WhenClauseNameDoesNotAlreadyExist_CreatesClause() {
        var clauseForCreation = Mockito.mock(ClauseForCreation.class);
        var clause = Mockito.mock(Clause.class);
        Mockito.when(dao.create(clauseForCreation)).thenReturn(clause);
        Mockito.when(dao.nameExists(Mockito.any())).thenReturn(false);

        var result = service.create(clauseForCreation);

        assertEquals(clause, result, "Created clause should be returned from service");
    }

    @Test
    void create_WhenClauseNameAlreadyExists_ThrowsBadRequestException() {
        var clauseForCreation = Mockito.mock(ClauseForCreation.class);
        Mockito.when(clauseForCreation.name()).thenReturn("test");
        Mockito.when(dao.nameExists(clauseForCreation.name())).thenReturn(true);

        var e = assertThrows(BadRequestException.class, () -> service.create(clauseForCreation));

        assertEquals("Clause with name 'test' already exists", e.getDetailedError(),
                "Exception message should indicate duplicate clause name");
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


}
