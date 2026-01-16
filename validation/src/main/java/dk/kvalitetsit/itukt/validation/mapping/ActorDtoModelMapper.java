package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import org.openapitools.model.Actor;
import org.openapitools.model.DepartmentIdentifier;

import java.util.Optional;
import java.util.Set;

public class ActorDtoModelMapper implements Mapper<Actor, ValidationInput.Actor> {
    private final Cache<Department.Identifier, Department> departmentCache;

    public ActorDtoModelMapper(Cache<Department.Identifier, Department> departmentCache) {
        this.departmentCache = departmentCache;
    }

    @Override
    public ValidationInput.Actor map(Actor actor) {
        Optional<Department> department = actor.getDepartmentIdentifier()
                .map(this::map)
                .map(this::getDepartment);
        return new ValidationInput.Actor(actor.getIdentifier(), actor.getSpecialityCode(), department);
    }

    private Department getDepartment(Department.Identifier id) {
        return departmentCache.get(id)
                .orElse(new Department(id, Set.of()));
    }

    private Department.Identifier map(DepartmentIdentifier id) {
        return switch (id.getType()) {
            case SOR -> new Department.Identifier.SOR(id.getCode());
            case SHAK -> new Department.Identifier.SHAK(id.getCode());
        };
    }
}
