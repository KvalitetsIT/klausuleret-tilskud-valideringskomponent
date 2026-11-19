package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.exceptions.NotFoundApiException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
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
    void deleteDraft_whenSuccess_thenReturnDeletedClause() throws NotFoundException {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var expected = Mockito.mock(Clause.class);

        UUID uuid = UUID.randomUUID();

        Mockito.when(concreteRepository.deleteDraft(uuid)).thenReturn(clauseEntity);
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(expected);

        var actual = adaptor.deleteDraft(uuid);

        assertEquals(expected, actual);
    }

    @Test
    void deleteDraft_whenThrowsNotFoundException_thenThrow() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        Mockito.when(concreteRepository.deleteDraft(uuid)).thenThrow(NotFoundApiException.class);
        Assertions.assertThrows(NotFoundApiException.class, () -> adaptor.deleteDraft(uuid));
    }

    @Test
    void deleteDraft_whenThrowsException_ThrowsException() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        var expectedException = new RuntimeException("test");
        Mockito.when(concreteRepository.deleteDraft(uuid)).thenThrow(expectedException);

        var e = assertThrows(RuntimeException.class, () -> adaptor.deleteDraft(uuid));
        assertEquals(expectedException, e);
    }

    @Test
    void readParent() {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        UUID uuid = UUID.randomUUID();
        Mockito.when(concreteRepository.readParent(uuid)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.readParent(uuid);

        assertEquals(Optional.of(clause), result);
    }
}


