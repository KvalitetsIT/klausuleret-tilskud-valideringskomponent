package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;

public interface DrugClauseViewRepository {
    List<DrugClauseView> findAll() throws ServiceException;
}
