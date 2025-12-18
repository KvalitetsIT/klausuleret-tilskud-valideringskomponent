package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DrugClauseViewMapper;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.List;

public class DrugClauseViewRepositoryAdaptor implements Repository<DrugClause> {

    private final DrugClauseViewMapper drugClauseViewMapper;
    private final Repository<DrugClauseView> repository;

    public DrugClauseViewRepositoryAdaptor(DrugClauseViewMapper drugClauseViewMapper, Repository<DrugClauseView> repository) {
        this.drugClauseViewMapper = drugClauseViewMapper;
        this.repository = repository;
    }

    @Override
    public List<DrugClause> fetchAll() throws ServiceException {
        return drugClauseViewMapper.map(repository.fetchAll());
    }
}
