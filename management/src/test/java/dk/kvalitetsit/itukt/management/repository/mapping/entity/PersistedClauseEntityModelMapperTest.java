package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PersistedClauseEntityModelMapperTest {

    @Mock
    private Mapper<ExpressionEntity.Persisted, Expression.Persisted> expressionEntityModelMapper;

    @InjectMocks
    private PersistedClauseEntityModelMapper persistedClauseEntityModelMapper;

    @BeforeEach
    void setup() {
        this.persistedClauseEntityModelMapper = new PersistedClauseEntityModelMapper(expressionEntityModelMapper);
        Mockito.when(expressionEntityModelMapper.map(MockFactory.EXPRESSION_1_ENTITY)).thenReturn(MockFactory.EXPRESSION_1_MODEL);
    }

    @Test
    public void testMapping() {
        assertEquals(MockFactory.CLAUSE_1_MODEL, persistedClauseEntityModelMapper.map(MockFactory.CLAUSE_1_ENTITY));
    }
}