package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.management.MockFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClauseEntityModelMapperTest {

    @Mock
    private ExpressionEntityModelMapper expressionEntityModelMapper;

    @InjectMocks
    private ClauseEntityModelMapper clauseEntityModelMapper;

    @BeforeEach
    void setup() {
        this.clauseEntityModelMapper = new ClauseEntityModelMapper(expressionEntityModelMapper);
        Mockito.when(expressionEntityModelMapper.map(MockFactory.EXPRESSION_1_ENTITY)).thenReturn(MockFactory.EXPRESSION_1_MODEL);
    }

    @Test
    public void testMapping(){
        assertEquals(MockFactory.CLAUSE_1_MODEL, clauseEntityModelMapper.map(MockFactory.CLAUSE_1_ENTITY));
    }
}