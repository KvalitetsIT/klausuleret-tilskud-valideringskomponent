package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentSpecialityExpressionValidatorTest {

    @Mock
    private StamdataCacheService<Department.Speciality> departmentSpecialityService;

    @InjectMocks
    private DepartmentSpecialityExpressionValidator validator;

    @Test
    void validate_WhenSpecialityIsKnown_ReturnsNoErrors() {
        var speciality = new Department.Speciality("A");
        when(departmentSpecialityService.get(speciality.name())).thenReturn(Optional.of(speciality));

        var result = validator.validate(new DepartmentSpecialityConditionExpression(speciality.name()));

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenSpecialityIsUnknown_ReturnsUnknownDepartmentSpecialityError() {
        when(departmentSpecialityService.get(Mockito.any())).thenReturn(Optional.empty());

        var result = validator.validate(new DepartmentSpecialityConditionExpression("B"));

        var expected = List.of(new UnknownValueError(Identifier.DEPARTMENT_SPECIALITY, "B"));
        assertEquals(expected, result);
    }
}

