package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.Repository;
import dk.kvalitetsit.itukt.validation.service.model.Department;
import dk.kvalitetsit.itukt.validation.service.model.DepartmentIdentifier;
import dk.kvalitetsit.itukt.validation.service.model.Speciality;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepartmentCache extends Cache<Department, DepartmentIdentifier> {
    public DepartmentCache(CacheConfiguration configuration, Repository<Department> repository) {
        super(configuration, repository);
    }

    @Override
    Department resolveConflict(Department x, Department y) {
        Set<Speciality> specialities = Stream.concat(x.specialities().stream(), y.specialities().stream()).collect(Collectors.toSet());
        return new Department(x.departmentIdentifier(), specialities);
    }

    @Override
    DepartmentIdentifier getIdentifier(Department item) {
        return item.departmentIdentifier();
    }
}
