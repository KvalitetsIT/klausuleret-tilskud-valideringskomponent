package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpressionModelDtoMapperTest {

    @InjectMocks
    private ExpressionModelDtoMapper mapper;

    @Test
    void map() {
        Assertions.assertEquals(MockFactory.EXPRESSION_1_DTO, this.mapper.map(MockFactory.EXPRESSION_1_MODEL));
    }

}