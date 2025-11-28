package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.DepartmentEntity;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class DepartmentRepositoryImpl implements DepartmentRepository<DepartmentEntity> {

    @Override
    public List<DepartmentEntity> findAll() throws ServiceException {
        throw new NotImplementedException();
    }
}
