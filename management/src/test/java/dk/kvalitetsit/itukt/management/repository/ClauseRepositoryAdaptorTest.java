package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
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
    private Mapper<ClauseEntity.Persisted, Clause.Persisted> persistedMapper;


    @Mock
    private Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> notPersistedMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                persistedMapper,
                notPersistedMapper
        );
    }

    @Test
    void testCreate() {
        var outputClause = Mockito.mock(Clause.Persisted.class);
        var clauseEntity = Mockito.mock(ClauseEntity.Persisted.class);

        ClauseEntity.NotPersisted clauseForCreationEntity = Mockito.mock(ClauseEntity.NotPersisted.class);

        Clause.NotPersisted clauseForCreationModel = Mockito.mock(Clause.NotPersisted.class);
        Mockito.when(notPersistedMapper.map(Mockito.any(Clause.NotPersisted.class))).thenReturn(clauseForCreationEntity);
        Mockito.when(persistedMapper.map(clauseEntity)).thenReturn(outputClause);
        Mockito.when(concreteRepository.create(Mockito.any(ClauseEntity.NotPersisted.class))).thenReturn(clauseEntity);

        var result = adaptor.create(clauseForCreationModel);

        assertEquals(outputClause, result);

        Mockito.verify(concreteRepository, Mockito.times(1)).create(clauseForCreationEntity);
    }

    @Test
    void testRead() {
        var uuid = UUID.randomUUID();
        var clauseEntity = Mockito.mock(ClauseEntity.Persisted.class);
        var clause = Mockito.mock(Clause.Persisted.class);
        Mockito.when(concreteRepository.read(uuid)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(persistedMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.read(uuid);

        assertEquals(clause, result.get());
    }

    @Test
    void testReadAll() {

        var clauseEntity = Mockito.mock(ClauseEntity.Persisted.class);
        var clause = Mockito.mock(Clause.Persisted.class);

        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(clauseEntity));

        Mockito.when(persistedMapper.map(List.of(clauseEntity))).thenReturn(List.of(clause));

        var result = adaptor.readAll();
        assertEquals(List.of(clause), result);
    }
}


