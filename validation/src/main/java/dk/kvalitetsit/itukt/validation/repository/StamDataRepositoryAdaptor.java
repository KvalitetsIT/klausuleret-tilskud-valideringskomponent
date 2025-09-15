package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.StamData;

import java.util.List;

public class StamDataRepositoryAdaptor implements StamDataRepository<StamData> {

    private final Mapper<StamDataEntity, StamData> stamDataMapper;
    private final StamDataRepository<StamDataEntity> repository;

    public StamDataRepositoryAdaptor(Mapper<StamDataEntity, StamData> stamDataMapper, StamDataRepository<StamDataEntity> repository) {
        this.stamDataMapper = stamDataMapper;
        this.repository = repository;
    }

    @Override
    public List<StamData> findAll() throws ServiceException {
        return stamDataMapper.map(repository.findAll());
    }
}
