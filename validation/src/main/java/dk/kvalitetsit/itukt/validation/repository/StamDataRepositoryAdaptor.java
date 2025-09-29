package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.mapping.StamDataMapper;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.Map;

public class StamDataRepositoryAdaptor {

    private final StamDataMapper stamDataMapper;
    private final StamDataRepository repository;

    public StamDataRepositoryAdaptor(StamDataMapper stamDataMapper, StamDataRepository repository) {
        this.stamDataMapper = stamDataMapper;
        this.repository = repository;
    }

    public Map<Long, StamData> findAll() throws ServiceException {
        return stamDataMapper.map(repository.findAll());
    }
}
