package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

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
class ExpressionModelDslMapperTest {

    @InjectMocks
    private ExpressionModelDslMapper mapper;

    @Test
    void map() {
        assertEquals(MockFactory.EXPRESSION_1_DSL, this.mapper.map(MockFactory.EXPRESSION_1_MODEL));
    }
}