package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.mapping.StamDataMapper;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.List;

public class StamDataRepositoryAdaptor implements Repository<StamData> {

    private final StamDataMapper stamDataMapper;
    private final Repository<StamDataEntity> repository;

    public StamDataRepositoryAdaptor(StamDataMapper stamDataMapper, Repository<StamDataEntity> repository) {
        this.stamDataMapper = stamDataMapper;
        this.repository = repository;
    }

    public List<StamData> findAll() throws ServiceException {
        return stamDataMapper.map(repository.findAll());
    }
}
