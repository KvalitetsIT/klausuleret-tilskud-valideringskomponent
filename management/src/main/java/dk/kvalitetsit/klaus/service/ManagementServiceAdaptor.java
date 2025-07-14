package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;
import org.openapitools.model.Clause;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagementServiceAdaptor {

    private final ManagementService<dk.kvalitetsit.klaus.model.Clause> clauseService;
    private final Mapper<Clause, dk.kvalitetsit.klaus.model.Clause> dtoMapper;
    private final Mapper<dk.kvalitetsit.klaus.model.Clause, Clause> modelMapper;
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
        this.dtoMapper = dtoModelMapper;
        this.modelMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDslMapper = clauseDslMapper;
    }

    public List<String> createDSL(List<String> dsl) throws ServiceException {
        return dsl.stream().map(this::createDSL).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public List<Clause> create(List<Clause> expressions) throws ServiceException {
        return expressions.stream().map(this::create).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public Optional<Clause> create(Clause entry) throws ServiceException {
        var model = dtoMapper.map(entry);
        return clauseService.create(model).map(modelMapper::map);
    }

    public Optional<String> createDSL(String dsl) throws ServiceException {
        var clause = this.dslClauseMapper.map(dsl);
        var model = this.dtoMapper.map(clause);
        return clauseService.create(model).map(clauseDslMapper::map);
    }


    public Optional<Clause> delete(UUID entry) throws ServiceException {
        return clauseService.delete(entry).map(modelMapper::map);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseService.read(id).map(modelMapper::map);
    }

    public Optional<String> read_dsl(UUID id) throws ServiceException {
        return clauseService.read(id).map(clauseDslMapper::map);
    }

    public List<Clause> read_all(Pagination pagination) throws ServiceException {
        return clauseService.read_all(pagination).stream().map(modelMapper::map).toList();
    }

    public List<Clause> read_all() throws ServiceException {
        return clauseService.read_all().stream().map(modelMapper::map).toList();
    }

    public Optional<Clause> update(UUID id, Clause entry) throws ServiceException {
        return clauseService.update(id, dtoMapper.map(entry)).map(modelMapper::map);
    }
}
