package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ClauseDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_DTO;
import static dk.kvalitetsit.itukt.management.MockFactory.CLAUSE_1_MODEL;
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
    private ClauseDtoModelMapper clauseDtoModelMapper;

    @Mock
    private ClauseDslModelMapper clauseDslModelMapper;

    @Mock
    private ClauseModelDslMapper clauseModelDslMapper;

    @BeforeEach
    void setUp() {
        adaptor = new ManagementServiceAdaptor(managementServiceImpl, clauseDtoModelMapper, clauseModelDtoMapper, clauseDslModelMapper, clauseModelDslMapper);
        Mockito.when(clauseModelDtoMapper.map(CLAUSE_1_MODEL)).thenReturn(CLAUSE_1_DTO);
    }

    @Test
    void testCreate() {
        Mockito.when(clauseDtoModelMapper.map(CLAUSE_1_DTO)).thenReturn(CLAUSE_1_MODEL);
        Mockito.when(managementServiceImpl.create(Mockito.any(Clause.class))).thenReturn(CLAUSE_1_MODEL);
        var result = adaptor.create(CLAUSE_1_DTO);
        assertEquals(CLAUSE_1_DTO, result);

        var captor = ArgumentCaptor.forClass(Clause.class);
        Mockito.verify(managementServiceImpl, Mockito.times(1)).create(captor.capture());
        Clause actual_model = captor.getValue();

        assertEquals(CLAUSE_1_MODEL, actual_model);
    }

    @Test
    void testRead() {
        var uuid = CLAUSE_1_MODEL.uuid().get();
        Mockito.when(managementServiceImpl.read(uuid)).thenReturn(Optional.of(CLAUSE_1_MODEL));
        var result = adaptor.read(uuid);
        assertEquals(CLAUSE_1_DTO, result.get());
    }

    @Test
    void testReadAll() {
        Mockito.when(managementServiceImpl.readAll()).thenReturn(List.of(CLAUSE_1_MODEL));
        var result = adaptor.read_all();
        assertEquals(List.of(CLAUSE_1_DTO), result);
    }
}


