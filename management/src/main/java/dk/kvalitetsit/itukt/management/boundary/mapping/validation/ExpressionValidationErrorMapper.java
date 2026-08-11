package dk.kvalitetsit.itukt.management.boundary.mapping.validation;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;

public class ExpressionValidationErrorMapper implements Mapper<ExpressionValidationError, String> {
    @Override
    public String map(ExpressionValidationError error) {
        return switch (error) {
            case UnknownDepartmentSpecialityError e -> "Ukendt afdelingsspeciale " + e.departmentSpeciality();
        };
    }
}
