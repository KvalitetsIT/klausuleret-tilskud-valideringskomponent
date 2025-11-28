package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.service.model.Department;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class DepartmentRepositoryAdaptor implements DepartmentRepository<Department> {

    @Override
    public List<Department> findAll() throws ServiceException {
        throw new NotImplementedException();

    }
}
