package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Operator;


@ExtendWith(MockitoExtension.class)
class OperatorModelDtoMapperTest {

    @InjectMocks
    private OperatorModelDtoMapper mapper;

    @Test
    void map() {
        Assertions.assertEquals(Operator.EQUAL, this.mapper.map(dk.kvalitetsit.itukt.common.model.Operator.EQUAL));
        Assertions.assertEquals(Operator.GREATER_THAN, this.mapper.map(dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN));
        Assertions.assertEquals(Operator.GREATER_THAN_OR_EQUAL_TO, this.mapper.map(dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO));
        Assertions.assertEquals(Operator.LESS_THAN, this.mapper.map(dk.kvalitetsit.itukt.common.model.Operator.LESS_THAN));
    }
}