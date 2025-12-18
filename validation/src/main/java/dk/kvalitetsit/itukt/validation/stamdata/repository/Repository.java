package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;

public interface Repository<T> {
    List<T> fetchAll() throws ServiceException;
}
