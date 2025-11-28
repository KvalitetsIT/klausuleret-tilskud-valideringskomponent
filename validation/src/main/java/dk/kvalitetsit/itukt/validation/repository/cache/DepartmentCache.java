package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.validation.repository.Repository;
import dk.kvalitetsit.itukt.validation.service.model.Department;
import dk.kvalitetsit.itukt.validation.service.model.SOR;
import dk.kvalitetsit.itukt.validation.service.model.Speciality;

import java.util.List;
import java.util.stream.Stream;

public class DepartmentCache extends Cache<Department, SOR> {
    public DepartmentCache(CacheConfiguration configuration, Repository<Department> repository) {
        super(configuration, repository);
    }

    @Override
    Department resolveConflict(Department x, Department y) {
        List<Speciality> specialities = Stream.concat(x.specialities().stream(), y.specialities().stream()).toList();
        return new Department(x.sor(), specialities);
    }

    @Override
    SOR getIdentifier(Department item) {
        return item.sor();
    }
}
