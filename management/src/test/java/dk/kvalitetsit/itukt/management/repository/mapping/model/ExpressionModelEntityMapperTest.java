package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.management.MockFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpressionModelEntityMapperTest {

    @InjectMocks
    private ExpressionModelEntityMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ExpressionModelEntityMapper();
    }

    @Test
    public void testMapping() {
        Assertions.assertThat(mapper.map(MockFactory.EXPRESSION_1_MODEL))
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id", "right.left.id", "right.right.id")
                .isEqualTo(MockFactory.EXPRESSION_1_ENTITY);
    }
}