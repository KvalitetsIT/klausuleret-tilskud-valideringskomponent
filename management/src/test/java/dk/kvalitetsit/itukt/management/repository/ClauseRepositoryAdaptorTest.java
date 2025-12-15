package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepository concreteRepository;

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
        var clauseForCreation = Mockito.mock(ClauseInput.class);
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
    void testReadAllActive() {

        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);

        Mockito.when(concreteRepository.readAllActive()).thenReturn(List.of(clauseEntity));

        Mockito.when(clauseEntityModelMapper.map(List.of(clauseEntity))).thenReturn(List.of(clause));

        var result = adaptor.readAllActive();
        assertEquals(List.of(clause), result);
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
    void nameExists() {
        String clauseName = "Test Clause";
        Mockito.when(concreteRepository.nameExists(clauseName)).thenReturn(true);

        boolean exists = adaptor.nameExists(clauseName);

        assertTrue(exists, "nameExists should return the same value as the concrete repository");
    }

    @Test
    void updateDraftToActive() {
        UUID uuid = UUID.randomUUID();

        adaptor.updateDraftToActive(uuid);

        Mockito.verify(concreteRepository).updateDraftToActive(uuid);
    }
}


