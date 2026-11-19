package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import dk.kvalitetsit.itukt.common.model.DoctorSpecialityConditionExpression;
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
class DoctorSpecialityExpressionValidatorTest {

    @Mock
    private StamdataCacheService<DoctorSpeciality> doctorSpecialityService;

    @InjectMocks
    private DoctorSpecialityExpressionValidator validator;

    @Test
    void validate_WhenSpecialityIsKnown_ReturnsNoErrors() {
        var speciality = new DoctorSpeciality("A");
        when(doctorSpecialityService.get(speciality.value())).thenReturn(Optional.of(speciality));

        var result = validator.validate(new DoctorSpecialityConditionExpression(speciality.value()));

        assertEquals(List.of(), result);
    }

    @Test
    void validate_WhenSpecialityIsUnknown_ReturnsUnknownDoctorSpecialityError() {
        when(doctorSpecialityService.get(Mockito.any())).thenReturn(Optional.empty());

        var result = validator.validate(new DoctorSpecialityConditionExpression("B"));

        var expected = List.of(new UnknownValueError(Identifier.DOCTOR_SPECIALITY, "B"));
        assertEquals(expected, result);
    }
}

