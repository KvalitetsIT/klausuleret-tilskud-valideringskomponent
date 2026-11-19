package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ExpressionValidationApiException;
import dk.kvalitetsit.itukt.management.exceptions.ExpressionValidationException;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.stream.Collectors;

public class ExpressionValidationExceptionMapper implements Mapper<ExpressionValidationException, ExpressionValidationApiException> {
    private final Mapper<ExpressionValidationError, String> expressionValidationErrorMapper;

    public ExpressionValidationExceptionMapper(Mapper<ExpressionValidationError, String> expressionValidationErrorMapper) {
        this.expressionValidationErrorMapper = expressionValidationErrorMapper;
    }

    @Override
    public ExpressionValidationApiException map(ExpressionValidationException e) {
        String errorMessage = e.getValidationErrors().stream()
                .map(expressionValidationErrorMapper::map)
                .collect(Collectors.joining("\n"));
        return new ExpressionValidationApiException(errorMessage);
    }
}
