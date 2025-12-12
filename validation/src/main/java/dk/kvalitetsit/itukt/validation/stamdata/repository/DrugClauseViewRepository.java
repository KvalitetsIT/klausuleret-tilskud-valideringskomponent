package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;

import java.util.List;

public interface DrugClauseViewRepository {
    List<DrugClauseView> findAll() throws ServiceException;
}
