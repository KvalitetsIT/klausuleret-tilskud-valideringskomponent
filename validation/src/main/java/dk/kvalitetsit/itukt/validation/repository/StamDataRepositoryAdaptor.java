package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.mapping.StamDataMapper;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.List;
import java.util.Map;

public class StamDataRepositoryAdaptor implements StamDataRepository<StamData> {

    private final StamDataMapper stamDataMapper;
    private final StamDataRepository<StamDataEntity> repository;

    public StamDataRepositoryAdaptor(StamDataMapper stamDataMapper, StamDataRepository<StamDataEntity> repository) {
        this.stamDataMapper = stamDataMapper;
        this.repository = repository;
    }

    public List<StamData> findAll() throws ServiceException {
        return stamDataMapper.map(repository.findAll());
    }
}
