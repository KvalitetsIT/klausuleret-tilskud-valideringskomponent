package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
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
        var speciality = new Department.Speciality("A");
        when(departmentSpecialityService.getSpeciality(speciality.name())).thenReturn(Optional.of(speciality));

        var result = validator.validate(new DepartmentSpecialityConditionExpression(speciality.name()));

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenSpecialityIsUnknown_ReturnsUnknownDepartmentSpecialityError() {
        var knownSpecialities = Set.of(new Department.Speciality("A"));
        when(departmentSpecialityService.getSpeciality(Mockito.any())).thenReturn(Optional.empty());
        when(departmentSpecialityService.getSpecialities()).thenReturn(knownSpecialities);

        var result = validator.validate(new DepartmentSpecialityConditionExpression("B"));

        var expected = List.of(new UnknownDepartmentSpecialityError("B", knownSpecialities));
        assertEquals(expected, result);
    }
}

