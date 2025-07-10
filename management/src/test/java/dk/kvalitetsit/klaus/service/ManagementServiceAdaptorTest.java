package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.boundary.mapping.DslMapper;
import dk.kvalitetsit.klaus.boundary.mapping.ExpressionToDslMapper;
import dk.kvalitetsit.klaus.boundary.mapping.DtoMapper;
import dk.kvalitetsit.klaus.boundary.mapping.ModelMapper;
import dk.kvalitetsit.klaus.model.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BinaryExpression;

import java.util.List;

import static dk.kvalitetsit.klaus.MockFactory.clause;
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
        var modelMapper = new ModelMapper();
        var dtoMapper = new DtoMapper();
        var dslMapper = new DslMapper();
        var dslModelMapper = new ExpressionToDslMapper();

        adaptor = new ManagementServiceAdaptor(concreteService, dtoMapper, modelMapper, dslMapper, dslModelMapper);
    }

    @Test
    void testCreate() {
        var expected_model = new Expression.BinaryExpression(
                new Expression.Condition("ATC", "=", List.of("C10BA03")),
                "eller",
                new Expression.BinaryExpression(
                        new Expression.Condition("ATC", "i", List.of("C10BA02", "C10BA05")),
                        "og",
                        new Expression.Condition("ALDER", ">=", List.of("13"))
                )
        );

        Mockito.when(concreteService.create(Mockito.any(Expression.class))).thenReturn(expected_model);
        var result = adaptor.create(clause);

        // Verify correct result
        assertInstanceOf(clause.getClass(), result);
        BinaryExpression condition = (BinaryExpression) result;

        assertEquals(clause.getType(), condition.getType());


        // Verify correct argument
        var captor = ArgumentCaptor.forClass(Expression.BinaryExpression.class);
        Mockito.verify(concreteService, Mockito.times(1)).create(captor.capture());
        Expression.BinaryExpression actual_model = captor.getValue();

        assertEquals(expected_model, actual_model);

    }
}


