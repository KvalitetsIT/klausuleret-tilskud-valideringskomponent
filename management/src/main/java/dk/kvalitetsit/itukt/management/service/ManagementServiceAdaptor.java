package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.ClauseOutput;
import org.openapitools.model.DslInput;
import org.openapitools.model.DslOutput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceAdaptor {

    private final ManagementService clauseService;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> clauseDtoMapper;
    private final Mapper<String, ClauseInput> dslClauseMapper;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Clause, DslOutput> clauseDslMapper;
    private final Mapper<ClauseInput, ClauseForCreation> clauseInputMapper;

    public ManagementServiceAdaptor(
            ManagementService clauseService,
            Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> modelDtoMapper,
            Mapper<String, ClauseInput> dslClauseMapper,
            Mapper<dk.kvalitetsit.itukt.common.model.Clause, DslOutput> clauseDslMapper,
            Mapper<ClauseInput, ClauseForCreation> clauseInputMapper
    ) {
        this.clauseService = clauseService;
        this.clauseDtoMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDslMapper = clauseDslMapper;
        this.clauseInputMapper = clauseInputMapper;
    }

    public ClauseOutput create(ClauseInput clauseInput) throws ServiceException {
        var clauseForCreation = clauseInputMapper.map(clauseInput);
        return clauseDtoMapper.map(clauseService.create(clauseForCreation));
    }

    public DslOutput createDSL(DslInput dsl) throws ServiceException {
        var clauseInput = this.dslClauseMapper.map(dsl.getDsl());
        var clauseForCreation = clauseInputMapper.map(clauseInput);
        return clauseDslMapper.map(clauseService.create(clauseForCreation));
    }

    public Optional<ClauseOutput> read(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDtoMapper::map);
    }

    public Optional<DslOutput> readDsl(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDslMapper::map);
    }

    public List<ClauseOutput> readAll() throws ServiceException {
        return clauseDtoMapper.map(clauseService.readAll());
    }

    public List<DslOutput> readAllDsl() throws ServiceException {
        return clauseDslMapper.map(clauseService.readAll());
    }
}
