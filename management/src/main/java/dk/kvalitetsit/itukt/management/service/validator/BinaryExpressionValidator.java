package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;
import java.util.stream.Stream;

public class BinaryExpressionValidator implements ExpressionValidator<BinaryExpression> {
    private final ExpressionValidator<Expression> expressionValidator;

    public BinaryExpressionValidator(ExpressionValidator<Expression> expressionValidator) {
        this.expressionValidator = expressionValidator;
    }

    @Override
    public List<ExpressionValidationError> validate(BinaryExpression expression) {
        var leftErrors = expressionValidator.validate(expression.left());
        var rightErrors = expressionValidator.validate(expression.right());
        return Stream.concat(leftErrors.stream(), rightErrors.stream()).toList();
    }
}
