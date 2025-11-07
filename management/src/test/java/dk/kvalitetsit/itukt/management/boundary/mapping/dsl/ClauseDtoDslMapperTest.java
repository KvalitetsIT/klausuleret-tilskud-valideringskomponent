package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.ClauseDtoDslMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Expression;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ClauseDtoDslMapperTest {

    @InjectMocks
    private ClauseDtoDslMapper mapper;

    @Mock
    private Mapper<Expression, String> expressionModelDslMapper;

    @BeforeEach
    void setup() {
        this.mapper = new ClauseDtoDslMapper(expressionModelDslMapper);
        Mockito.when(expressionModelDslMapper.map(MockFactory.CLAUSE_1_INPUT.getExpression())).thenReturn(MockFactory.EXPRESSION_1_DSL);
    }

    @Test
    void map() {
        assertEquals(MockFactory.CLAUSE_1_DSL_OUTPUT, this.mapper.map(MockFactory.CLAUSE_1_OUTPUT));
    }
}