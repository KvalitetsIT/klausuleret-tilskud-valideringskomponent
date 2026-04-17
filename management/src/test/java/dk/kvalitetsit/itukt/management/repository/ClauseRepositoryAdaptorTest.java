package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseInputModelEntityMapper;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepository concreteRepository;

    @Mock
    private ClauseEntityModelMapper clauseEntityModelMapper;

    @Mock
    private ClauseInputModelEntityMapper clauseInputMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                clauseEntityModelMapper,
                clauseInputMapper
        );
    }

    @Test
    void testCreate() {
        var outputClause = Mockito.mock(Clause.class);
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clauseInput = Mockito.mock(ClauseFullInput.class);
        var expectedClauseEntityInput = Mockito.mock(ClauseEntityInput.class);
        Mockito.when(clauseInputMapper.map(clauseInput)).thenReturn(expectedClauseEntityInput);
        Mockito.when(concreteRepository.create(expectedClauseEntityInput))
                .thenReturn(clauseEntity);
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(outputClause);

        var result = adaptor.create(clauseInput);

        assertEquals(outputClause, result);

        Mockito.verify(concreteRepository, Mockito.times(1)).create(expectedClauseEntityInput);
    }

    @Test
    void testRead() {
        var uuid = UUID.randomUUID();
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        Mockito.when(concreteRepository.read(uuid)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.read(uuid);

        assertEquals(clause, result.get());
    }

    @Test
    void testReadCurrentClauses() {

        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);

        Mockito.when(concreteRepository.readCurrentClauses()).thenReturn(List.of(clauseEntity));

        Mockito.when(clauseEntityModelMapper.map(List.of(clauseEntity))).thenReturn(List.of(clause));

        var result = adaptor.readCurrentClauses();
        assertEquals(List.of(clause), result);
    }

    @Test
    void testReadCurrentClause() {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        String name = "test";
        Mockito.when(concreteRepository.readCurrentClause(name)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.readCurrentClause(name);

        assertEquals(Optional.of(clause), result);
    }

    @Test
    void testReadAllDrafts() {

        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);

        Mockito.when(concreteRepository.readAllDrafts()).thenReturn(List.of(clauseEntity));

        Mockito.when(clauseEntityModelMapper.map(List.of(clauseEntity))).thenReturn(List.of(clause));

        var result = adaptor.readAllDrafts();
        assertEquals(List.of(clause), result);
    }

    @Test
    void updateDraftToActive() {
        UUID uuid = UUID.randomUUID();

        adaptor.updateDraftToActive(uuid);

        Mockito.verify(concreteRepository).updateDraftToActive(uuid);
    }

    @Test
    void delete_whenSuccess_thenReturnDeletedClause() {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var expected = Mockito.mock(Clause.class);

        UUID uuid = UUID.randomUUID();

        Mockito.when(concreteRepository.delete(uuid)).thenReturn(clauseEntity);
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(expected);

        var actual = adaptor.delete(uuid);

        assertEquals(expected, actual);
    }

    @Test
    void delete_whenThrows_thenThrow() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(concreteRepository.delete(uuid)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> adaptor.delete(uuid));
    }

    @Test
    void delete_givenStatusNotDraft_ThrowsException() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(concreteRepository.delete(uuid)).thenThrow(ServiceException.class);
        Assertions.assertThrows(ServiceException.class, () -> adaptor.delete(uuid));
    }
}


