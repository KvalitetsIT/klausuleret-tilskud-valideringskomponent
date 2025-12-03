package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.openapitools.model.ClauseOutput;
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

    public List<ClauseOutput> readAll() throws ServiceException {
        return clauseDtoMapper.map(clauseService.readAll());
    }

    public List<DslOutput> readAllDsl() throws ServiceException {
        List<Clause> clauses = clauseService.readAll();
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clauses));
    }
}
