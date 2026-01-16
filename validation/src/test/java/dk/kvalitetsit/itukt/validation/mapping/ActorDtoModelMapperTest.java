package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Actor;
import org.openapitools.model.DepartmentIdentifier;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ActorDtoModelMapperTest {
    @InjectMocks
    private ActorDtoModelMapper actorDtoModelMapper;
    @Mock
    private Cache<Department.Identifier, Department> departmentCache;

    @Test
    void map_WithShakIdAndSpecialitiesInCache_ReturnsActorWithIdAndSpecialities() {
        var departmentId = new DepartmentIdentifier()
                .code("12345")
                .type(DepartmentIdentifier.TypeEnum.SHAK);
        var actor = new Actor()
                .identifier("test-id")
                .specialityCode("test-speciality")
                .departmentIdentifier(departmentId);
        var mappedDepartmentId = new Department.Identifier.SHAK(departmentId.getCode());
        var department = Mockito.mock(Department.class);
        Mockito.when(departmentCache.get(mappedDepartmentId))
                .thenReturn(Optional.of(department));

        var mappedActor = actorDtoModelMapper.map(actor);

        var expectedActor = new ValidationInput.Actor(
                actor.getIdentifier(),
                actor.getSpecialityCode(),
                Optional.of(department)
        );
        assertEquals(expectedActor, mappedActor);
    }

    @Test
    void map_WithSorIdAndSpecialitiesInCache_ReturnsActorWithIdAndSpecialities() {
        var departmentId = new DepartmentIdentifier()
                .code("12345")
                .type(DepartmentIdentifier.TypeEnum.SOR);
        var actor = new Actor()
                .identifier("test-id")
                .specialityCode("test-speciality")
                .departmentIdentifier(departmentId);
        var mappedDepartmentId = new Department.Identifier.SOR(departmentId.getCode());
        var department = Mockito.mock(Department.class);
        Mockito.when(departmentCache.get(mappedDepartmentId))
                .thenReturn(Optional.of(department));

        var mappedActor = actorDtoModelMapper.map(actor);

        var expectedActor = new ValidationInput.Actor(
                actor.getIdentifier(),
                actor.getSpecialityCode(),
                Optional.of(department)
        );
        assertEquals(expectedActor, mappedActor);
    }

    @Test
    void map_WithNoSpecialitiesInCache_ReturnsActorWithoutSpecialities() {
        var departmentId = new DepartmentIdentifier()
                .code("12345")
                .type(DepartmentIdentifier.TypeEnum.SOR);
        var actor = new Actor()
                .identifier("test-id")
                .specialityCode("test-speciality")
                .departmentIdentifier(departmentId);
        var mappedDepartmentId = new Department.Identifier.SOR(departmentId.getCode());
        Mockito.when(departmentCache.get(mappedDepartmentId))
                .thenReturn(Optional.empty());

        var mappedActor = actorDtoModelMapper.map(actor);

        var expectedActor = new ValidationInput.Actor(
                actor.getIdentifier(),
                actor.getSpecialityCode(),
                Optional.of(new Department(mappedDepartmentId, Set.of()))
        );
        assertEquals(expectedActor, mappedActor);
    }

    @Test
    void map_WithoutDepartmentId_ReturnsActorWithoutDepartment() {
        var actor = new Actor()
                .identifier("test-id")
                .specialityCode("test-speciality");

        var mappedActor = actorDtoModelMapper.map(actor);

        var expectedActor = new ValidationInput.Actor(
                actor.getIdentifier(),
                actor.getSpecialityCode(),
                Optional.empty()
        );
        assertEquals(expectedActor, mappedActor);
    }
}