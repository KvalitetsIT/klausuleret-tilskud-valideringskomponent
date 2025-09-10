package dk.kvalitetsit.itukt.management.boundary.mapping;


import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionModelDslMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static dk.kvalitetsit.itukt.management.MockFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DslClauseMapperTest {

    private final ClauseDslModelMapper mapper = new ClauseDslModelMapper();
    private final ClauseModelDslMapper clauseModelDslMapper = new ClauseModelDslMapper(new ExpressionModelDslMapper());

    @Test
    void testDSLToModel() {
        assertEquals(CLAUSE_1_DTO.getExpression(), mapper.map(CLAUSE_1_DSL).getExpression());
    }

    @Test
    @Disabled("IUAKT-109 retter op på problemer med parenteser samt gruppering af conditions")
    void testModelToDSL() {
        assertEquals(CLAUSE_1_DSL, clauseModelDslMapper.map(CLAUSE_1_MODEL));
    }

}
