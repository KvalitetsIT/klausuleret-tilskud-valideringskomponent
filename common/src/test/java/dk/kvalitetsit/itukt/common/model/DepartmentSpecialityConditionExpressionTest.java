package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DepartmentSpecialityConditionExpressionTest {


    @Test
    public void validates_returnEmptyWhenSpecialityConditionIsMeet() {
        DepartmentSpecialityConditionExpression subject = new DepartmentSpecialityConditionExpression("læge");
        var input = mock(ValidationInput.class);

        var department = mock(Department.class);

        Department.Speciality speciality = new Department.Speciality("læge");

        ValidationInput.Actor creator = mock(ValidationInput.Actor.class);
        Mockito.when(input.createdBy()).thenReturn(creator);
        Mockito.when(creator.department()).thenReturn(Optional.of(department));

        Mockito.when(department.specialities()).thenReturn(Set.of(speciality));
        assertEquals(Optional.empty(), subject.validates(input));

    }

    @Test
    public void validates_returnValidationErrorWhenSpecialityDoesNotExist() {
        String requiredSpeciality = "overlæge";

        DepartmentSpecialityConditionExpression subject = new DepartmentSpecialityConditionExpression(requiredSpeciality);

        var input = mock(ValidationInput.class);
        var creatorDepartment = mock(Department.class);
        var reporterDepartment = mock(Department.class);

        ValidationInput.Actor creator = mock(ValidationInput.Actor.class);
        ValidationInput.Actor reporter = mock(ValidationInput.Actor.class);

        Mockito.when(input.createdBy()).thenReturn(creator);
        Mockito.when(input.reportedBy()).thenReturn(Optional.of(reporter));


        Mockito.when(creator.department()).thenReturn(Optional.of(creatorDepartment));
        Mockito.when(reporter.department()).thenReturn(Optional.of(reporterDepartment));
        Mockito.when(creatorDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("læge")));
        Mockito.when(reporterDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("sygeplejerske")));

        var expected = Optional.of(new ValidationError.ConditionError(
                ValidationError.ConditionError.Field.DEPARTMENT_SPECIALITY,
                Operator.EQUAL,
                requiredSpeciality
        ));

        assertEquals(expected, subject.validates(input));

    }

    @Test
    public void validates_WithNoDepartmentInValidationInput_ReturnsValidationError() {
        var requiredSpeciality = "overlæge";
        var subject = new DepartmentSpecialityConditionExpression(requiredSpeciality);
        var input = mock(ValidationInput.class);
        var creator = mock(ValidationInput.Actor.class);
        Mockito.when(input.createdBy()).thenReturn(creator);
        Mockito.when(input.reportedBy()).thenReturn(Optional.empty());
        Mockito.when(creator.department()).thenReturn(Optional.empty());

        var result = subject.validates(input);

        var expected = Optional.of(new ValidationError.ConditionError(
                ValidationError.ConditionError.Field.DEPARTMENT_SPECIALITY,
                Operator.EQUAL,
                requiredSpeciality
        ));
        assertEquals(expected, result);
    }



    @Test
    public void validates_returnSuccessWhenSpecialityEitherCreatorOrReportHasTheRequiredSpeciality() {
        String requiredSpeciality = "sygeplejerske";

        DepartmentSpecialityConditionExpression subject = new DepartmentSpecialityConditionExpression(requiredSpeciality);

        var input = mock(ValidationInput.class);
        var creatorDepartment = mock(Department.class);
        var reporterDepartment = mock(Department.class);

        ValidationInput.Actor creator = mock(ValidationInput.Actor.class);
        ValidationInput.Actor reporter = mock(ValidationInput.Actor.class);

        Mockito.when(creator.department()).thenReturn(Optional.of(creatorDepartment));
        Mockito.when(reporter.department()).thenReturn(Optional.of(reporterDepartment));
        Mockito.when(creatorDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("læge")));
        Mockito.when(reporterDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("sygeplejerske")));

        Mockito.when(input.createdBy()).thenReturn(creator);
        Mockito.when(input.reportedBy()).thenReturn(Optional.of(reporter));

        var expected = Optional.empty();

        assertEquals(expected, subject.validates(input));


        Mockito.when(creatorDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("sygeplejerske")));
        Mockito.when(reporterDepartment.specialities()).thenReturn(Set.of(new Department.Speciality("læge")));

        assertEquals(expected, subject.validates(input));

    }


}