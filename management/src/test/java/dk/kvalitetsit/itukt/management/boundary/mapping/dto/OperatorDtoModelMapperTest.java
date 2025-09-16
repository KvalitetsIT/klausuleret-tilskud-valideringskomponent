package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.model.Operator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OperatorDtoModelMapperTest {

    @InjectMocks
    private OperatorDtoModelMapper mapper;

    @Test
    void map() {
        assertEquals(Operator.EQUAL, mapper.map(org.openapitools.model.Operator.EQUAL));
        assertEquals(Operator.GREATER_THAN_OR_EQUAL_TO, mapper.map(org.openapitools.model.Operator.GREATER_THAN_OR_EQUAL_TO));
        assertEquals(Operator.GREATER_THAN, mapper.map(org.openapitools.model.Operator.GREATER_THAN));
        assertEquals(Operator.LESS_THAN, mapper.map(org.openapitools.model.Operator.LESS_THAN));
        assertEquals(Operator.LESS_THAN_OR_EQUAL_TO, mapper.map(org.openapitools.model.Operator.LESS_THAN_OR_EQUAL_TO));
    }
}