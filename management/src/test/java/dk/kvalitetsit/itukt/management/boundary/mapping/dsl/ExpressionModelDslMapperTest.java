package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ExpressionModelDslMapperTest {

    @InjectMocks
    private ExpressionModelDslMapper mapper;

    @Test
    @Disabled("IUAKT-109 retter op på problemer med parenteser samt gruppering af conditions")
    void map() {
        assertEquals(MockFactory.EXPRESSION_1_DSL, this.mapper.map(MockFactory.EXPRESSION_1_MODEL));
    }
}