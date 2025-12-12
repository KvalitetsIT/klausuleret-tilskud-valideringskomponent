package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DrugClauseViewMapper;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.Map;

public class DrugClauseViewRepositoryAdaptor {

    private final DrugClauseViewMapper drugClauseViewMapper;
    private final DrugClauseViewRepository repository;

    public DrugClauseViewRepositoryAdaptor(DrugClauseViewMapper drugClauseViewMapper, DrugClauseViewRepository repository) {
        this.drugClauseViewMapper = drugClauseViewMapper;
        this.repository = repository;
    }

    public Map<Long, DrugClause> findAll() throws ServiceException {
        return drugClauseViewMapper.map(repository.findAll());
    }
}
