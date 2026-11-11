package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.model.ValidationError;

import static java.lang.String.join;

public class ConditionErrorMapper implements ValidationErrorMapper<ValidationError.ConditionError> {
    @Override
    public String map(ValidationError.ConditionError error) {
        return join(" ",
                toErrorString(error.field()),
                toErrorString(error.operator()),
                error.value()
        );
    }

    private String toErrorString(Operator operator) {
        return switch (operator) {
            case EQUAL -> "skal være";
            case GREATER_THAN_OR_EQUAL_TO -> "skal være større end eller lig";
            case LESS_THAN_OR_EQUAL_TO -> "skal være mindre end eller lig";
            case GREATER_THAN -> "skal være større end";
            case LESS_THAN -> "skal være mindre end";
        };
    }

    private String toErrorString(ValidationError.ConditionError.Field field) {
        return switch (field) {
            case AGE -> "alder";
            case INDICATION -> "indikation";
            case DOCTOR_SPECIALITY -> "lægespeciale";
            case DEPARTMENT_SPECIALITY -> "afdelingens speciale";
        };
    }
}
