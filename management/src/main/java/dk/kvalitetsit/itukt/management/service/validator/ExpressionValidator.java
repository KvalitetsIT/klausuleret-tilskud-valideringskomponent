package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;

public interface ExpressionValidator<T extends Expression> {
    List<ExpressionValidationError> validate(T expression);
}
