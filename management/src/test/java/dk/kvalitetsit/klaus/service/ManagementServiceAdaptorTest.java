package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.boundary.mapping.*;
import dk.kvalitetsit.klaus.model.Clause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BinaryExpression;

import java.util.Optional;

import static dk.kvalitetsit.klaus.MockFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
public class ManagementServiceAdaptorTest {

    @InjectMocks
    private ManagementServiceAdaptor adaptor;

    @Mock
    private ManagementServiceImpl concreteService;


    @BeforeEach
    void setUp() {
        var modelMapper = new ClauseModelMapper(new ExpressionModelMapper());
        var clauseMapper = new DtoClauseMapper(new ExpressionMapper());
        var dslMapper = new DslMapper();
        var dslModelMapper = new ClauseDslMapper(new ExpressionDslMapper());

        adaptor = new ManagementServiceAdaptor(concreteService, clauseMapper, modelMapper, dslMapper, dslModelMapper);
    }

    @Test
    void testCreate() {

        Mockito.when(concreteService.create(Mockito.any(Clause.class))).thenReturn(Optional.of(clauseModel));
        var result = adaptor.create(clauseDto);

        assertInstanceOf(expressionDto.getClass(), result.get().getExpression());
        BinaryExpression condition = (BinaryExpression) result.get().getExpression();

        assertEquals(expressionDto.getType(), condition.getType());

        var captor = ArgumentCaptor.forClass(Clause.class);
        Mockito.verify(concreteService, Mockito.times(1)).create(captor.capture());
        Clause actual_model = captor.getValue();

        var expected_model = new Clause(clauseModel.name(), Optional.empty(), clauseModel.expression());
        assertEquals(expected_model, actual_model);
    }
}


