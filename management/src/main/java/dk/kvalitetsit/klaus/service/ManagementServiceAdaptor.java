package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import org.openapitools.model.Clause;
import org.openapitools.model.DslInput;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagementServiceAdaptor {

    private final ManagementService<dk.kvalitetsit.klaus.model.Clause> clauseService;
    private final Mapper<Clause, dk.kvalitetsit.klaus.model.Clause> dtoClauseMapper;
    private final Mapper<dk.kvalitetsit.klaus.model.Clause, Clause> clauseDtoMapper;
    private final Mapper<String, Clause> dslClauseMapper;
    private final Mapper<dk.kvalitetsit.klaus.model.Clause, String> clauseDslMapper;

    public ManagementServiceAdaptor(
            ManagementService<dk.kvalitetsit.klaus.model.Clause> clauseService,
            Mapper<Clause, dk.kvalitetsit.klaus.model.Clause> dtoModelMapper,
            Mapper<dk.kvalitetsit.klaus.model.Clause, Clause> modelDtoMapper,
            Mapper<String, Clause> dslClauseMapper,
            Mapper<dk.kvalitetsit.klaus.model.Clause, String> clauseDslMapper
    ) {
        this.clauseService = clauseService;
        this.dtoClauseMapper = dtoModelMapper;
        this.clauseDtoMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDslMapper = clauseDslMapper;
    }

    public Optional<Clause> create(Clause entry) throws ServiceException {
        var model = dtoClauseMapper.map(entry);
        return clauseService.create(model).map(clauseDtoMapper::map);
    }

    public Optional<String> createDSL(DslInput dsl) throws ServiceException {
        var clause = this.dslClauseMapper.map(dsl.getDsl());
        var model = this.dtoClauseMapper.map(clause);
        return clauseService.create(model).map(clauseDslMapper::map);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDtoMapper::map);
    }

    public Optional<String> read_dsl(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDslMapper::map);
    }

    public List<Clause> read_all() throws ServiceException {
        return clauseService.readAll().stream().map(clauseDtoMapper::map).toList();
    }
}
