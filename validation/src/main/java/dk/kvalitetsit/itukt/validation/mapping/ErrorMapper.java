package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Clause;
import org.openapitools.model.ValidationError;

public class ErrorMapper implements Mapper<dk.kvalitetsit.itukt.validation.service.model.ValidationError, ValidationError> {
    @Override
    public ValidationError map(dk.kvalitetsit.itukt.validation.service.model.ValidationError modelValidationError) {
        return new ValidationError()
                .elementPath(modelValidationError.elementPath())
                .code(modelValidationError.code())
                .message(modelValidationError.message())
                .clause(new Clause()
                        .message(modelValidationError.clause().message())
                        .code(modelValidationError.clause().code())
                        .text(modelValidationError.clause().text())
                );
    }
}
