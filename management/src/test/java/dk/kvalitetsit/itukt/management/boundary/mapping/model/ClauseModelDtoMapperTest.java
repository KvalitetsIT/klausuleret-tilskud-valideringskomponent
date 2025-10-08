package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
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
import org.openapitools.model.Error;


@ExtendWith(MockitoExtension.class)
class ClauseModelDtoMapperTest {

    @InjectMocks
    private ClauseModelDtoMapper mapper;

    @Mock
    private Mapper<Expression, org.openapitools.model.Expression> expressionMapper;

    @Mock
    private Mapper<Clause.Error, Error> errorMapper;

    @BeforeEach
    void setup() {
        this.mapper = new ClauseModelDtoMapper(expressionMapper, errorMapper);
        Mockito.when(expressionMapper.map(MockFactory.CLAUSE_1_MODEL.expression())).thenReturn(MockFactory.EXPRESSION_1_DTO);
        Mockito.when(errorMapper.map(MockFactory.CLAUSE_1_MODEL.error())).thenReturn(MockFactory.CLAUSE_1_OUTPUT.getError());
    }

    @Test
    void map() {
        Assertions.assertEquals(MockFactory.CLAUSE_1_OUTPUT, this.mapper.map(MockFactory.CLAUSE_1_MODEL));
    }

}