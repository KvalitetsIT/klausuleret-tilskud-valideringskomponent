package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ClauseModelDslMapperTest {

    @InjectMocks
    private ClauseModelDslMapper mapper;

    @Mock
    private ExpressionModelDslMapper expressionModelDslMapper;

    @BeforeEach
    void setup(){
        Mockito.when(expressionModelDslMapper.map(MockFactory.CLAUSE_1_MODEL.expression())).thenReturn(MockFactory.EXPRESSION_1_DSL);
    }

    @Test
    void map() {
        assertEquals(MockFactory.CLAUSE_1_DSL, this.mapper.map(MockFactory.CLAUSE_1_MODEL));
    }
}