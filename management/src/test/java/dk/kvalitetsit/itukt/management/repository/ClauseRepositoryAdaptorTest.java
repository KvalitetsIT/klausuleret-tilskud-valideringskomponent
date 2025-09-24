package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
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

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepositoryImpl concreteRepository;

    @Mock
    private ClauseEntityModelMapper clauseEntityModelMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                clauseEntityModelMapper
        );
    }

    @Test
    void testCreate() {
        var outputClause = Mockito.mock(Clause.class);
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clauseForCreation = Mockito.mock(ClauseForCreation.class);
        Mockito.when(concreteRepository.create(clauseForCreation)).thenReturn(clauseEntity);
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(outputClause);

        var result = adaptor.create(clauseForCreation);

        assertEquals(outputClause, result);

        Mockito.verify(concreteRepository, Mockito.times(1)).create(clauseForCreation);
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
    void testReadAll() {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.readAll();
        assertEquals(List.of(clause), result);
    }
}


