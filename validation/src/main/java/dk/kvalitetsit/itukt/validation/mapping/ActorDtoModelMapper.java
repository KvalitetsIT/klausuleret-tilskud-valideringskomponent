package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.openapitools.model.Actor;

import java.util.Optional;
import java.util.Set;

public class ActorDtoModelMapper implements Mapper<Actor, ValidationInput.Actor> {
    @Override
    public ValidationInput.Actor map(Actor actor) {
        Optional<Department> department = actor.getDepartmentIdentifier().map(x -> switch (x.getType()) {
            case SOR -> new Department(
                    Optional.empty(),
                    Optional.of(new Department.Identifier.SOR(x.getCode())),
                    Set.of()
            );
            case SHAK -> new Department(
                    Optional.of(
                            new Department.Identifier.SHAK(x.getCode())),
                    Optional.empty(),
                    Set.of()
            );
        });
        return new ValidationInput.Actor(actor.getIdentifier(), actor.getSpecialityCode(), department);
    }
}
