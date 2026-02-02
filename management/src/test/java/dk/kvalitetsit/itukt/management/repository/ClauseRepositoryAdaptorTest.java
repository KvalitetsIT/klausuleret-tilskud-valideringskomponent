package dk.kvalitetsit.itukt.management.repository;


import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionModelEntityMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ClauseRepositoryAdaptorTest {

    @InjectMocks
    private ClauseRepositoryAdaptor adaptor;

    @Mock
    private ClauseRepository concreteRepository;

    @Mock
    private ClauseEntityModelMapper clauseEntityModelMapper;

    @Mock
    private ExpressionModelEntityMapper expressionMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ClauseRepositoryAdaptor(
                concreteRepository,
                clauseEntityModelMapper,
                expressionMapper
        );
    }

    @Test
    void testCreate() {
        var outputClause = Mockito.mock(Clause.class);
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var expression = Mockito.mock(BinaryExpression.class);
        String name = "test";
        String errorMessage = "test error";
        var status = Clause.Status.DRAFT;
        var validFrom = new Date();
        var expressionEntity = Mockito.mock(ExpressionEntity.StringConditionEntity.class);
        Mockito.when(expressionMapper.map(expression)).thenReturn(expressionEntity);
        Mockito.when(concreteRepository.create(name, expressionEntity, errorMessage, status, validFrom))
                .thenReturn(clauseEntity);
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(outputClause);

        var result = adaptor.create(name, expression, errorMessage, status, validFrom);

        assertEquals(outputClause, result);

        Mockito.verify(concreteRepository, Mockito.times(1)).create(name, expressionEntity, errorMessage, status, validFrom);
    }

    @Test
    void testRead() {
        var uuid = UUID.randomUUID();
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        Mockito.when(concreteRepository.readLatestVersion(uuid)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.read(uuid);

        assertEquals(clause, result.get());
    }

    @Test
    void testReadLatestVersions() {

        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);

        Mockito.when(concreteRepository.readLatestVersions()).thenReturn(List.of(clauseEntity));

        Mockito.when(clauseEntityModelMapper.map(List.of(clauseEntity))).thenReturn(List.of(clause));

        var result = adaptor.readLatestVersions();
        assertEquals(List.of(clause), result);
    }

    @Test
    void testReadLatestVersion() {
        var clauseEntity = Mockito.mock(ClauseEntity.class);
        var clause = Mockito.mock(Clause.class);
        String name = "test";
        Mockito.when(concreteRepository.readLatestVersion(name)).thenReturn(Optional.of(clauseEntity));
        Mockito.when(clauseEntityModelMapper.map(clauseEntity)).thenReturn(clause);

        var result = adaptor.readLatestVersion(name);

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
}


