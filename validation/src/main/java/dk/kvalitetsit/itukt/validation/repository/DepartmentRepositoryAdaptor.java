package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.DepartmentEntity;
import dk.kvalitetsit.itukt.common.model.Department;

import java.util.List;

public class DepartmentRepositoryAdaptor implements Repository<Department> {
    private final Mapper<DepartmentEntity, Department> mapper;
    private final Repository<DepartmentEntity> repository;

    public DepartmentRepositoryAdaptor(Mapper<DepartmentEntity, Department> mapper, Repository<DepartmentEntity> repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public List<Department> findAll() throws ServiceException {
        return mapper.map(repository.findAll());
    }
}
