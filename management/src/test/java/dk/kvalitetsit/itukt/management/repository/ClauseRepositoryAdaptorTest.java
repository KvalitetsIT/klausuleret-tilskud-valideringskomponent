package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityClauseMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityExpressionMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseEntityMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionEntityMapper;
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

import static dk.kvalitetsit.itukt.management.MockFactory.clauseEntity;
import static dk.kvalitetsit.itukt.management.MockFactory.clauseModel;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepositoryImpl concreteRepository;


    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                new ClauseEntityMapper(new ExpressionEntityMapper()),
                new EntityClauseMapper(new EntityExpressionMapper())
        );
    }

    @Test
    void testCreate() {
        Mockito.when(concreteRepository.create(Mockito.any(ClauseEntity.class))).thenReturn(Optional.of(MockFactory.clauseEntity));
        var result = adaptor.create(clauseModel);
        assertEquals(clauseModel, result.get());

        var captor = ArgumentCaptor.forClass(ClauseEntity.class);
        Mockito.verify(concreteRepository, Mockito.times(1)).create(captor.capture());
        ClauseEntity actual_model = captor.getValue();

        assertThat(actual_model)
                .usingRecursiveComparison()
                .ignoringFields("id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                .isEqualTo(MockFactory.clauseEntity);
    }

    @Test
    void testRead() {
        var uuid = clauseModel.uuid().get();
        Mockito.when(concreteRepository.read(uuid)).thenReturn(Optional.of(clauseEntity));
        var result = adaptor.read(uuid);
        assertEquals(clauseModel, result.get());
    }

    @Test
    void testReadAll() {
        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(clauseEntity));
        var result = adaptor.readAll();
        assertEquals(List.of(clauseModel), result);
    }


}


