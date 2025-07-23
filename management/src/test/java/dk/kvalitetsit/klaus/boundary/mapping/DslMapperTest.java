package dk.kvalitetsit.klaus.boundary.mapping;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static dk.kvalitetsit.klaus.MockFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DslMapperTest {

    private final DslMapper mapper = new DslMapper();
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
