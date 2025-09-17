package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.StringConditionExpression;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ManagementServiceAdaptorTest {

    @InjectMocks
    private ManagementServiceAdaptor adaptor;

    @Mock
    private ManagementServiceImpl managementServiceImpl;

    @Mock
    private ExpressionDtoModelMapper expressionMapper;

    @Mock
    private ClauseModelDtoMapper clauseModelDtoMapper;

    @Mock
    private ClauseDslModelMapper clauseDslModelMapper;

    @Mock
    private ClauseModelDslMapper clauseModelDslMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ManagementServiceAdaptor(managementServiceImpl, expressionMapper, clauseModelDtoMapper, clauseDslModelMapper, clauseModelDslMapper);
    }

    @Test
    void testCreate() {
        var clauseInput = new ClauseInput("testName", Mockito.mock(Expression.class));
        var expression = Mockito.mock(StringConditionExpression.class);
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(expressionMapper.map(clauseInput.getExpression())).thenReturn(expression);
        Mockito.when(managementServiceImpl.create(clauseInput.getName(), expression)).thenReturn(clause);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.create(clauseInput);

        assertEquals(clauseOutput, result);
    }

    @Test
    void testCreateDsl() {
        var dslInput = new DslInput("test");
        var clauseInput = new ClauseInput("testName", Mockito.mock(Expression.class));
        var expression = Mockito.mock(StringConditionExpression.class);
        var clause = Mockito.mock(Clause.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(clauseDslModelMapper.map(dslInput.getDsl())).thenReturn(clauseInput);
        Mockito.when(expressionMapper.map(clauseInput.getExpression())).thenReturn(expression);
        Mockito.when(managementServiceImpl.create(clauseInput.getName(), expression)).thenReturn(clause);
        Mockito.when(clauseModelDslMapper.map(clause)).thenReturn(dslOutput);

        var result = adaptor.createDSL(dslInput);

        assertEquals(dslOutput, result);
    }

    @Test
    void testRead() {
        var uuid = UUID.randomUUID();
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(managementServiceImpl.read(uuid)).thenReturn(Optional.of(clause));
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.read(uuid);

        assertEquals(clauseOutput, result.get());
    }

    @Test
    void testReadAll() {
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(managementServiceImpl.readAll()).thenReturn(List.of(clause));
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.readAll();

        assertEquals(List.of(clauseOutput), result);
    }
}


