package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.StamDataEntity;

import java.util.List;

public interface StamDataRepository {
    List<StamDataEntity> findAll() throws ServiceException;
}
