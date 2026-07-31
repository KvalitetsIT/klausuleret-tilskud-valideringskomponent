package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslDtoMapper;
import dk.kvalitetsit.itukt.management.exceptions.*;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ManagementServiceAdaptorTest {

    @InjectMocks
    private ManagementServiceAdaptor adaptor;

    @Mock
    private ManagementService managementServiceImpl;

    @Mock
    private Mapper<Clause, org.openapitools.model.ClauseOutput> clauseModelDtoMapper;

    @Mock
    private ClauseDslDtoMapper clauseDslDtoMapper;

    @Mock
    private Mapper<ClauseOutput, DslOutput> clauseDtoDslMapper;

    @Mock
    private Mapper<org.openapitools.model.ClauseInput, ClauseInput> clauseInputMapper;

    @Mock
    private Mapper<ManagementException, ApiException> managementExceptionMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ManagementServiceAdaptor(
                managementServiceImpl,
                clauseModelDtoMapper,
                clauseDslDtoMapper,
                clauseDtoDslMapper,
                clauseInputMapper,
                managementExceptionMapper
        );
    }

    @Test
    void testCreate() throws ManagementException {
        var clauseInput = new org.openapitools.model.ClauseInput("testName", Mockito.mock(BinaryExpression.class), "Message");
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        var clauseForCreation = Mockito.mock(ClauseInput.class);
        Mockito.when(clauseInputMapper.map(clauseInput)).thenReturn(clauseForCreation);
        Mockito.when(managementServiceImpl.create(clauseForCreation)).thenReturn(clause);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.create(clauseInput);

        assertEquals(clauseOutput, result);
    }

    @Test
    void testCreateDsl() throws ManagementException {
        var dslInput = new DslInput("name", "message", "test");
        var clauseInput = new org.openapitools.model.ClauseInput("testName", Mockito.mock(BinaryExpression.class), "message");
        var clause = Mockito.mock(Clause.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        ClauseOutput clauseDto = Mockito.mock(ClauseOutput.class);
        var clauseForCreation = Mockito.mock(ClauseInput.class);

        Mockito.when(clauseInputMapper.map(clauseInput)).thenReturn(clauseForCreation);
        Mockito.when(clauseDslDtoMapper.map(dslInput)).thenReturn(clauseInput);
        Mockito.when(managementServiceImpl.create(clauseForCreation)).thenReturn(clause);
        Mockito.when(clauseDtoDslMapper.map(clauseDto)).thenReturn(dslOutput);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseDto);
        var result = adaptor.createDSL(dslInput);

        assertEquals(dslOutput, result);
    }

    @Test
    void createDSL_WhenDslParserExceptionIsThrown_MapsToApiException() throws DslParserException {
        var dslInput = new DslInput("name", "message", "test");
        var parserException = new UnexpectedValueException("Parsing error");
        Mockito.when(clauseDslDtoMapper.map(dslInput)).thenThrow(parserException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(parserException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.createDSL(dslInput));
        assertEquals(apiException, e);
    }

    @Test
    void testRead() {
        var uuid = UUID.randomUUID();
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(managementServiceImpl.read(uuid)).thenReturn(Optional.of(clause));
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);

        var result = adaptor.read(uuid);

        assertEquals(clauseOutput, result.orElseThrow());
    }

    @Test
    void testReadByStatus() {
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        Mockito.when(managementServiceImpl.readByStatus(Clause.Status.ACTIVE)).thenReturn(List.of(clause));
        Mockito.when(clauseModelDtoMapper.map(List.of(clause))).thenReturn(List.of(clauseOutput));

        var result = adaptor.readByStatus(ClauseStatus.ACTIVE);

        assertEquals(List.of(clauseOutput), result);
    }

    @Test
    void testReadDslByStatus() {
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(managementServiceImpl.readByStatus(Clause.Status.DRAFT)).thenReturn(List.of(clause));
        Mockito.when(clauseModelDtoMapper.map(List.of(clause))).thenReturn(List.of(clauseOutput));
        Mockito.when(clauseDtoDslMapper.map(List.of(clauseOutput))).thenReturn(List.of(dslOutput));

        var result = adaptor.readDslByStatus(ClauseStatus.DRAFT);

        assertEquals(List.of(dslOutput), result);
    }

    @Test
    void approveClause_WithUuid_ApprovesClause() throws ManagementException {
        var uuid = UUID.randomUUID();
        adaptor.approveClause(uuid, false);
        Mockito.verify(managementServiceImpl, Mockito.times(1)).approve(uuid, false);
    }

    @Test
    void approveClause_WhenNotFoundExceptionIsThrown_MapsToApiException() throws ManagementException {
        var notFoundException = new NotFoundException("Clause not found");
        Mockito.when(managementServiceImpl.approve(Mockito.any(), Mockito.anyBoolean()))
                .thenThrow(notFoundException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(notFoundException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.approveClause(UUID.randomUUID(), false));
        assertEquals(apiException, e);
    }

    @Test
    void inactivateClause_WithName_InactivatesClause() throws ManagementException {
        String name = "test";
        var inactiveClause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(managementServiceImpl.inactivate(name)).thenReturn(inactiveClause);
        Mockito.when(clauseModelDtoMapper.map(inactiveClause)).thenReturn(clauseOutput);
        Mockito.when(clauseDtoDslMapper.map(clauseOutput)).thenReturn(dslOutput);

        var response = adaptor.inactivateClause(name);

        assertEquals(dslOutput, response);
    }

    @Test
    void inactivateClause_WhenInvalidInputExceptionIsThrown_MapsToApiException() throws ManagementException {
        var invalidInputException = new InvalidInputException("Invalid input");
        Mockito.when(managementServiceImpl.inactivate(Mockito.any())).thenThrow(invalidInputException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(invalidInputException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.inactivateClause("test"));
        assertEquals(apiException, e);
    }

    @Test
    void activateClause_WithName_ActivatesClause() throws ManagementException {
        String name = "test";
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        var dslOutput = Mockito.mock(DslOutput.class);
        Mockito.when(managementServiceImpl.activate(name)).thenReturn(clause);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);
        Mockito.when(clauseDtoDslMapper.map(clauseOutput)).thenReturn(dslOutput);

        var response = adaptor.activateClause(name);

        assertEquals(dslOutput, response);
    }

    @Test
    void activateClause_WhenInvalidInputExceptionIsThrown_MapsToApiException() throws ManagementException {
        var invalidInputException = new InvalidInputException("Invalid input");
        Mockito.when(managementServiceImpl.activate(Mockito.any())).thenThrow(invalidInputException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(invalidInputException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.activateClause("test"));
        assertEquals(apiException, e);
    }

    @Test
    void deleteDraft() throws ManagementException {
        var clause = Mockito.mock(Clause.class);
        var clauseOutput = Mockito.mock(ClauseOutput.class);
        UUID uuid = UUID.randomUUID();
        Mockito.when(managementServiceImpl.deleteDraft(uuid)).thenReturn(clause);
        Mockito.when(clauseModelDtoMapper.map(clause)).thenReturn(clauseOutput);
        var response = adaptor.deleteDraft(uuid);
        Mockito.verify(managementServiceImpl, Mockito.times(1)).deleteDraft(uuid);
        assertEquals(clauseOutput, response);
    }

    @Test
    void deleteDraft_WhenNotFoundExceptionIsThrown_MapsToApiException() throws ManagementException {
        var notFoundException = new NotFoundException("Clause not found");
        Mockito.when(managementServiceImpl.deleteDraft(Mockito.any()))
                .thenThrow(notFoundException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(notFoundException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.deleteDraft(UUID.randomUUID()));
        assertEquals(apiException, e);
    }

    @Test
    void getNumberOfDrugsForClause_ReturnsDrugCountFromService() {
        String clauseName = "testClause";
        long expectedDrugCount = 5L;
        Mockito.when(managementServiceImpl.getNumberOfDrugsForClause(clauseName)).thenReturn(expectedDrugCount);

        var result = adaptor.getNumberOfDrugsForClause(clauseName);

        assertEquals(expectedDrugCount, result.getDrugCount());
    }

    @Test
    void readHistoryDsl_ReturnsMappedHistory() throws ManagementException {
        String name = "test";
        var clauses = List.of(Mockito.mock(Clause.class));
        Mockito.when(managementServiceImpl.readHistory(name)).thenReturn(clauses);
        var clausesOutput = List.of(Mockito.mock(ClauseOutput.class));
        Mockito.when(clauseModelDtoMapper.map(clauses)).thenReturn(clausesOutput);
        var dslOutput = List.of(Mockito.mock(DslOutput.class));
        Mockito.when(clauseDtoDslMapper.map(clausesOutput)).thenReturn(dslOutput);

        var result = adaptor.readHistoryDsl(name);

        assertEquals(dslOutput, result);
    }

    @Test
    void readHistoryDsl_WhenNotFoundExceptionIsThrown_MapsToApiException() throws ManagementException {
        String name = "test";
        var notFoundException = new NotFoundException("Clause not found");
        Mockito.when(managementServiceImpl.readHistory(name)).thenThrow(notFoundException);
        var apiException = new BadRequestApiException("test");
        Mockito.when(managementExceptionMapper.map(notFoundException)).thenReturn(apiException);

        var e = assertThrows(BadRequestApiException.class, () -> adaptor.readHistoryDsl(name));
        assertEquals(apiException, e);
    }
}


