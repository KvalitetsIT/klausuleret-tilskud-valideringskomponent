package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;

public interface StamDataRepository<T> {

    List<T> findAll() throws ServiceException;
}
