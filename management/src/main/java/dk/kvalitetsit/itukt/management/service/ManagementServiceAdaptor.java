package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.ClauseOutput;
import org.openapitools.model.DslInput;
import org.openapitools.model.DslOutput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceAdaptor {

    private final ManagementService clauseService;
    private final Mapper<org.openapitools.model.Expression, Expression> expressionMapper;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> clauseDtoMapper;
    private final Mapper<String, ClauseInput> dslClauseMapper;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Clause, DslOutput> clauseDslMapper;

    public ManagementServiceAdaptor(
            ManagementService clauseService, Mapper<org.openapitools.model.Expression, Expression> expressionMapper,
            Mapper<dk.kvalitetsit.itukt.common.model.Clause, ClauseOutput> modelDtoMapper,
            Mapper<String, ClauseInput> dslClauseMapper,
            Mapper<dk.kvalitetsit.itukt.common.model.Clause, DslOutput> clauseDslMapper
    ) {
        this.clauseService = clauseService;
        this.expressionMapper = expressionMapper;
        this.clauseDtoMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDslMapper = clauseDslMapper;
    }

    public ClauseOutput create(ClauseInput entry) throws ServiceException {
        Expression expression = expressionMapper.map(entry.getExpression());
        return clauseDtoMapper.map(clauseService.create(entry.getName(), expression));
    }

    public DslOutput createDSL(DslInput dsl) throws ServiceException {
        var clause = this.dslClauseMapper.map(dsl.getDsl());
        Expression expression = expressionMapper.map(clause.getExpression());
        return clauseDslMapper.map(clauseService.create(clause.getName(), expression));
    }

    public Optional<ClauseOutput> read(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDtoMapper::map);
    }

    public Optional<DslOutput> readDsl(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDslMapper::map);
    }

    public List<ClauseOutput> readAll() throws ServiceException {
        return clauseService.readAll().stream().map(clauseDtoMapper::map).toList();
    }

    public List<DslOutput> readAllDsl() throws ServiceException {
        return clauseService.readAll().stream().map(clauseDslMapper::map).toList();
    }
}
