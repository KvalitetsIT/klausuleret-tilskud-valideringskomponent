package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.openapitools.model.ClauseOutput;
import org.openapitools.model.ClauseStatus;
import org.openapitools.model.DslInput;
import org.openapitools.model.DslOutput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceAdaptor {

    private final ManagementService clauseService;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> clauseDtoMapper;
    private final Mapper<DslInput, org.openapitools.model.ClauseInput> dslClauseMapper;
    private final Mapper<ClauseOutput, DslOutput> clauseDtoDslMapper;
    private final Mapper<org.openapitools.model.ClauseInput, ClauseInput> clauseInputMapper;

    public ManagementServiceAdaptor(
            ManagementService clauseService,
            Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> modelDtoMapper,
            Mapper<DslInput, org.openapitools.model.ClauseInput> dslClauseMapper,
            Mapper<ClauseOutput, DslOutput> clauseDtoDslMapper,
            Mapper<org.openapitools.model.ClauseInput, ClauseInput> clauseInputMapper
    ) {
        this.clauseService = clauseService;
        this.clauseDtoMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDtoDslMapper = clauseDtoDslMapper;
        this.clauseInputMapper = clauseInputMapper;

    }

    public ClauseOutput create(org.openapitools.model.ClauseInput clauseInput) throws ServiceException {
        var clauseForCreation = clauseInputMapper.map(clauseInput);
        return clauseDtoMapper.map(clauseService.create(clauseForCreation));
    }

    public ClauseOutput update(org.openapitools.model.ClauseInput clauseInput) throws ServiceException {
        var clauseForUpdate = clauseInputMapper.map(clauseInput);
        return clauseDtoMapper.map(clauseService.update(clauseForUpdate));
    }

    public DslOutput createDSL(DslInput dsl) throws ServiceException {
        var clauseInput = this.dslClauseMapper.map(dsl);
        return clauseDtoDslMapper.map(this.create(clauseInput));
    }

    public Optional<ClauseOutput> read(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDtoMapper::map);
    }

    public Optional<DslOutput> readDsl(UUID id) throws ServiceException {
        return clauseService
                .read(id)
                .map(clauseDtoMapper::map)
                .map(clauseDtoDslMapper::map);
    }

    public List<DslOutput> readHistoryDsl(String name) throws ServiceException {
        List<Clause> clauses = clauseService.readHistory(name);
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clauses));
    }

    public List<ClauseOutput> readByStatus(ClauseStatus status) throws ServiceException {
        return clauseDtoMapper.map(clauseService.readByStatus(mapStatus(status)));
    }

    public List<DslOutput> readDslByStatus(ClauseStatus status) throws ServiceException {
        List<Clause> clauses = clauseService.readByStatus(mapStatus(status));
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clauses));
    }

    public void approve(UUID clauseUuid) throws ServiceException {
        clauseService.approve(clauseUuid);
    }

    private Clause.Status mapStatus(ClauseStatus status) {
        return switch (status) {
            case DRAFT -> Clause.Status.DRAFT;
            case ACTIVE -> Clause.Status.ACTIVE;
        };
    }
}
