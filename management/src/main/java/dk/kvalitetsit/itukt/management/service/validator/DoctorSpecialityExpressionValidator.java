package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import dk.kvalitetsit.itukt.common.model.DoctorSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;

public class DoctorSpecialityExpressionValidator implements ExpressionValidator<DoctorSpecialityConditionExpression> {
    private final StamdataCacheService<DoctorSpeciality> doctorSpecialityService;

    public DoctorSpecialityExpressionValidator(StamdataCacheService<DoctorSpeciality> doctorSpecialityService) {
        this.doctorSpecialityService = doctorSpecialityService;
    }

    @Override
    public List<ExpressionValidationError> validate(DoctorSpecialityConditionExpression expression) {
        var speciality = doctorSpecialityService.get(expression.speciality());
        return speciality.isPresent() ? List.of()
                : List.of(
                new UnknownValueError(
                        Identifier.DOCTOR_SPECIALITY,
                        expression.speciality()
                ));
    }
}
