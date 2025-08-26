package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseModelEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_ENTITY;
import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_MODEL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepositoryImpl concreteRepository;

    @Mock
    private ClauseModelEntityMapper clauseModelEntityMapper;

    @Mock
    private ClauseEntityModelMapper clauseEntityModelMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                clauseModelEntityMapper,
                clauseEntityModelMapper
        );
        Mockito.when(clauseEntityModelMapper.map(CLAUSE_1_ENTITY)).thenReturn(CLAUSE_1_MODEL);
    }

    @Test
    void testCreate() {
        Mockito.when(clauseModelEntityMapper.map(CLAUSE_1_MODEL)).thenReturn(CLAUSE_1_ENTITY);
        Mockito.when(concreteRepository.create(Mockito.any(ClauseEntity.class))).thenReturn(Optional.of(MockFactory.CLAUSE_1_ENTITY));

        var result = adaptor.create(CLAUSE_1_MODEL);
        assertEquals(CLAUSE_1_MODEL, result.get());

        var captor = ArgumentCaptor.forClass(ClauseEntity.class);
        Mockito.verify(concreteRepository, Mockito.times(1)).create(captor.capture());
        ClauseEntity actual_model = captor.getValue();

        assertThat(actual_model)
                .usingRecursiveComparison()
                .ignoringFields("id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                .isEqualTo(MockFactory.CLAUSE_1_ENTITY);
    }

    @Test
    void testRead() {
        var uuid = CLAUSE_1_MODEL.uuid().get();
        Mockito.when(concreteRepository.read(uuid)).thenReturn(Optional.of(CLAUSE_1_ENTITY));
        var result = adaptor.read(uuid);
        assertEquals(CLAUSE_1_MODEL, result.get());
    }

    @Test
    void testReadAll() {
        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(CLAUSE_1_ENTITY));
        var result = adaptor.readAll();
        assertEquals(List.of(CLAUSE_1_MODEL), result);
    }
}


