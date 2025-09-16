package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ClauseModelDtoMapperTest {

    @InjectMocks
    private ClauseModelDtoMapper mapper;

    @Mock
    private Mapper<Expression, org.openapitools.model.Expression> mock;

    @BeforeEach
    void setup() {
        this.mapper = new ClauseModelDtoMapper(mock);
        Mockito.when(mock.map(MockFactory.CLAUSE_1_MODEL.expression())).thenReturn(MockFactory.EXPRESSION_1_DTO);
    }

    @Test
    void map() {
        Assertions.assertEquals(MockFactory.CLAUSE_1_OUTPUT, this.mapper.map(MockFactory.CLAUSE_1_MODEL));
    }

}