package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Error;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ClauseModelDslMapperTest {

    @InjectMocks
    private ClauseModelDslMapper mapper;

    @Mock
    private Mapper<Expression, String> expressionModelDslMapper;

    @Mock
    private Mapper<Clause.Error, Error> errorMapper;

    @BeforeEach
    void setup() {
        this.mapper = new ClauseModelDslMapper(expressionModelDslMapper, errorMapper);
        Mockito.when(expressionModelDslMapper.map(MockFactory.CLAUSE_1_MODEL.expression())).thenReturn(MockFactory.EXPRESSION_1_DSL);
        Mockito.when(errorMapper.map(MockFactory.CLAUSE_1_MODEL.error())).thenReturn(MockFactory.CLAUSE_1_DSL_OUTPUT.getError());
    }

    @Test
    void map() {
        assertEquals(MockFactory.CLAUSE_1_DSL_OUTPUT, this.mapper.map(MockFactory.CLAUSE_1_MODEL));
    }
}