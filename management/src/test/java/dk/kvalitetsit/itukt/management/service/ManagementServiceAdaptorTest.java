package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ClauseOutput;
import org.openapitools.model.*;
import org.openapitools.model.Error;

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
    private ClauseModelDtoMapper clauseModelDtoMapper;

    @Mock
    private ClauseDslModelMapper clauseDslModelMapper;

    @Mock
    private ClauseModelDslMapper clauseModelDslMapper;

    @Mock
    private Mapper<ClauseInput, ClauseForCreation> clauseInputMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ManagementServiceAdaptor(managementServiceImpl, clauseModelDtoMapper, clauseDslModelMapper, clauseModelDslMapper, clauseInputMapper);
    }

    @Test
    void testCreate() {
        var clauseInput = new ClauseInput("testName", Mockito.mock(BinaryExpression.class), new Error("Message"));
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        var clauseForCreation = Mockito.mock(ClauseForCreation.class);
        Mockito.when(clauseInputMapper.map(clauseInput)).thenReturn(clauseForCreation);
        Mockito.when(managementServiceImpl.create(clauseForCreation)).thenReturn(clause);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.create(clauseInput);

        assertEquals(clauseOutput, result);
    }

    @Test
    void testCreateDsl() {
        var dslInput = new DslInput(new Error("message"), "test");
        var clauseInput = new ClauseInput("testName", Mockito.mock(BinaryExpression.class), new Error("message"));
        var clause = Mockito.mock(Clause.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        var clauseForCreation = Mockito.mock(ClauseForCreation.class);
        Mockito.when(clauseInputMapper.map(clauseInput)).thenReturn(clauseForCreation);
        Mockito.when(clauseDslModelMapper.map(dslInput)).thenReturn(clauseInput);
        Mockito.when(managementServiceImpl.create(clauseForCreation)).thenReturn(clause);
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
        Mockito.when(clauseModelDtoMapper.map(List.of(clause))).thenReturn(List.of(clauseOutput));

        var result = adaptor.readAll();

        assertEquals(List.of(clauseOutput), result);
    }
}


