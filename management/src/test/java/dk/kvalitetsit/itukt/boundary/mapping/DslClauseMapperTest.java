package dk.kvalitetsit.itukt.boundary.mapping;


import dk.kvalitetsit.itukt.boundary.mapping.dsl.ClauseDslMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dsl.DslClauseMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dsl.ExpressionDslMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static dk.kvalitetsit.itukt.MockFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DslClauseMapperTest {

    private final DslClauseMapper mapper = new DslClauseMapper();
    private final ClauseDslMapper clauseDslMapper = new ClauseDslMapper(new ExpressionDslMapper());

    @Test
    void testDSLToModel() {
        assertEquals(clauseDto, mapper.map(dsl));
    }

    @Test
    void testModelToDSL() {
        assertEquals(dsl, clauseDslMapper.map(clauseModel));
    }

}
