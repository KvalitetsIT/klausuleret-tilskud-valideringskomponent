package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentSpecialityExpressionValidatorTest {

    @Mock
    private DepartmentSpecialityService departmentSpecialityService;

    @InjectMocks
    private DepartmentSpecialityExpressionValidator validator;

    @Test
    void validate_WhenSpecialityIsKnown_ReturnsNoErrors() {
        var speciality1 = new Department.Speciality("A");
        var speciality2 = new Department.Speciality("B");
        when(departmentSpecialityService.getSpecialities()).thenReturn(Set.of(speciality1, speciality2));

        var result = validator.validate(new DepartmentSpecialityConditionExpression(speciality1.name()));

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenSpecialityIsUnknown_ReturnsUnknownDepartmentSpecialityError() {
        var knownSpecialities = Set.of(new Department.Speciality("A"));
        when(departmentSpecialityService.getSpecialities()).thenReturn(knownSpecialities);

        var result = validator.validate(new DepartmentSpecialityConditionExpression("B"));

        var expected = List.of(new UnknownDepartmentSpecialityError(new Department.Speciality("B"), knownSpecialities));
        assertEquals(expected, result);
    }
}

